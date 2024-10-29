package com.datn.endless.services;

import com.datn.endless.dtos.AttributeDTO;
import com.datn.endless.dtos.AttributeValueDTO;
import com.datn.endless.entities.Attribute;
import com.datn.endless.entities.Attributevalue;
import com.datn.endless.repositories.AttributeRepository;
import com.datn.endless.repositories.AttributevalueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AttributeService {

    @Autowired
    private AttributeRepository attributeRepository;

    @Autowired
    private AttributevalueRepository attributeValueRepository;

    public List<AttributeDTO> getAllAttributes(String id, String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Attribute> attributePage;

        if (!StringUtils.hasText(id) && !StringUtils.hasText(name)) {
            attributePage = attributeRepository.findAll(pageable);
        } else if (StringUtils.hasText(id)) {
            Attribute attribute = attributeRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy thuộc tính."));
            return List.of(convertToDTO(attribute));
        } else {
            attributePage = attributeRepository.findByAttributeNameContainingIgnoreCase(name, pageable);
        }

        return attributePage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<AttributeValueDTO> getAllAttributeValues(String attributeId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Attributevalue> attributeValuePage;

        // Nếu có ID của AttributeValue, tìm kiếm trực tiếp
        if (StringUtils.hasText(attributeId)) {
            Attributevalue attributeValue = attributeValueRepository.findById(attributeId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy giá trị thuộc tính."));
            return List.of(convertToValueDTO(attributeValue));
        }

        if (StringUtils.hasText(attributeId)) {
            Attribute attribute = attributeRepository.findById(attributeId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy thuộc tính."));
            attributeValuePage = attributeValueRepository.findByAttribute(attribute, pageable);
        } else {
            attributeValuePage = attributeValueRepository.findAll(pageable);
        }

        return attributeValuePage.getContent().stream()
                .map(this::convertToValueDTO)
                .collect(Collectors.toList());
    }

    public AttributeDTO createAttribute(AttributeDTO attributeDTO) {
        if (!StringUtils.hasText(attributeDTO.getAttributeName())) {
            throw new RuntimeException("Tên thuộc tính không được bỏ trống.");
        }

        if (attributeRepository.existsByAttributeName(attributeDTO.getAttributeName())) {
            throw new RuntimeException("Thuộc tính đã tồn tại.");
        }

        Attribute attribute = new Attribute();
        attribute.setAttributeID(UUID.randomUUID().toString());
        attribute.setAttributeName(attributeDTO.getAttributeName());

        Attribute savedAttribute = attributeRepository.save(attribute);
        return convertToDTO(savedAttribute);
    }

    public AttributeDTO updateAttribute(String id, AttributeDTO attributeDTO) {
        if (!StringUtils.hasText(attributeDTO.getAttributeName())) {
            throw new RuntimeException("Tên thuộc tính không được bỏ trống.");
        }

        Attribute attribute = attributeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thuộc tính."));

        if (attributeRepository.existsByAttributeName(attributeDTO.getAttributeName())
                && !attribute.getAttributeName().equals(attributeDTO.getAttributeName())) {
            throw new RuntimeException("Thuộc tính đã tồn tại.");
        }

        attribute.setAttributeName(attributeDTO.getAttributeName());
        attributeRepository.save(attribute);

        return convertToDTO(attribute);
    }

    public void deleteAttribute(String id) {
        if (!attributeRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy thuộc tính.");
        }
        attributeRepository.deleteById(id);
    }

    public AttributeValueDTO createAttributeValue(String attributeId, AttributeValueDTO attributeValueDTO) {
        if (!StringUtils.hasText(attributeValueDTO.getAttributeValue())) {
            throw new RuntimeException("Giá trị thuộc tính không được bỏ trống.");
        }

        Attribute attribute = attributeRepository.findById(attributeId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thuộc tính."));

        if (attributeValueRepository.existsByValueAndAttribute(attributeValueDTO.getAttributeValue(), attribute)) {
            throw new RuntimeException("Giá trị thuộc tính đã tồn tại cho thuộc tính này.");
        }

        Attributevalue attributeValue = new Attributevalue();
        attributeValue.setAttributeValueID(UUID.randomUUID().toString());
        attributeValue.setValue(attributeValueDTO.getAttributeValue());
        attributeValue.setAttribute(attribute);

        attributeValueRepository.save(attributeValue);
        return convertToValueDTO(attributeValue);
    }

    public AttributeValueDTO updateAttributeValue(String valueId, AttributeValueDTO attributeValueDTO) {
        if (!StringUtils.hasText(attributeValueDTO.getAttributeValue())) {
            throw new RuntimeException("Giá trị thuộc tính không được bỏ trống.");
        }

        Attributevalue attributeValue = attributeValueRepository.findById(valueId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giá trị thuộc tính."));

        if (attributeValueRepository.existsByValueAndAttribute(attributeValueDTO.getAttributeValue(), attributeValue.getAttribute())
                && !attributeValue.getValue().equals(attributeValueDTO.getAttributeValue())) {
            throw new RuntimeException("Giá trị thuộc tính đã tồn tại cho thuộc tính này.");
        }

        attributeValue.setValue(attributeValueDTO.getAttributeValue());
        attributeValueRepository.save(attributeValue);
        return convertToValueDTO(attributeValue);
    }

    public void deleteAttributeValue(String valueId) {
        if (!attributeValueRepository.existsById(valueId)) {
            throw new RuntimeException("Không tìm thấy giá trị thuộc tính.");
        }
        attributeValueRepository.deleteById(valueId);
    }

    private AttributeDTO convertToDTO(Attribute attribute) {
        AttributeDTO attributeDTO = new AttributeDTO();
        attributeDTO.setAttributeID(attribute.getAttributeID());
        attributeDTO.setAttributeName(attribute.getAttributeName());

        List<Attributevalue> values = attributeValueRepository.findByAttribute(attribute);
        attributeDTO.setAttributeValues(values.stream()
                .map(this::convertToValueDTO)
                .collect(Collectors.toList()));

        return attributeDTO;
    }

    private AttributeValueDTO convertToValueDTO(Attributevalue attributeValue) {
        AttributeValueDTO dto = new AttributeValueDTO();
        dto.setAttributeValueID(attributeValue.getAttributeValueID());
        dto.setAttributeValue(attributeValue.getValue());
        return dto;
    }
}
