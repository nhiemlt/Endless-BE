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

        // Tìm kiếm theo ID
        if (id != null && !id.isEmpty()) {
            Attribute attribute = attributeRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Attribute not found"));
            return List.of(convertToDTO(attribute));
        }

        // Tìm kiếm theo tên hoặc tên tiếng Anh
        if (name != null && !name.isEmpty()) {
            attributePage = attributeRepository.findByAttributeNameContainingIgnoreCase(name, pageable);
        }  else {
            attributePage = attributeRepository.findAll(pageable);
        }

        return attributePage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }



    private AttributeDTO convertToDTO(Attribute attribute) {
        AttributeDTO attributeDTO = new AttributeDTO();
        attributeDTO.setAttributeID(attribute.getAttributeID());
        attributeDTO.setAttributeName(attribute.getAttributeName());


        // Lấy giá trị cho thuộc tính này
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

    public AttributeDTO createAttribute(AttributeDTO attributeDTO) {
        Attribute attribute = new Attribute();
        attribute.setAttributeID(UUID.randomUUID().toString());
        attribute.setAttributeName(attributeDTO.getAttributeName());


        Attribute savedAttribute = attributeRepository.save(attribute);
        return convertToDTO(savedAttribute);
    }

    public AttributeDTO updateAttribute(String id, AttributeDTO attributeDTO) {
        Attribute attribute = attributeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Attribute not found"));

        attribute.setAttributeName(attributeDTO.getAttributeName());

        attributeRepository.save(attribute);

        return convertToDTO(attribute);
    }

    public void deleteAttribute(String id) {
        if (!attributeRepository.existsById(id)) {
            throw new RuntimeException("Attribute not found");
        }
        attributeRepository.deleteById(id);
    }

    public AttributeValueDTO createAttributeValue(String attributeId, AttributeValueDTO attributeValueDTO) {
        Attribute attribute = attributeRepository.findById(attributeId)
                .orElseThrow(() -> new RuntimeException("Attribute not found"));

        Attributevalue attributeValue = new Attributevalue();
        attributeValue.setAttributeValueID(UUID.randomUUID().toString());
        attributeValue.setValue(attributeValueDTO.getAttributeValue());
        attributeValue.setAttribute(attribute); // Thiết lập thuộc tính

        attributeValueRepository.save(attributeValue);
        return convertToValueDTO(attributeValue);
    }

    public AttributeValueDTO updateAttributeValue(String valueId, AttributeValueDTO attributeValueDTO) {
        Attributevalue attributeValue = attributeValueRepository.findById(valueId)
                .orElseThrow(() -> new RuntimeException("Attribute Value not found"));

        attributeValue.setValue(attributeValueDTO.getAttributeValue());

        attributeValueRepository.save(attributeValue);
        return convertToValueDTO(attributeValue);
    }

    public void deleteAttributeValue(String valueId) {
        if (!attributeValueRepository.existsById(valueId)) {
            throw new RuntimeException("Attribute Value not found");
        }
        attributeValueRepository.deleteById(valueId);
    }
}
