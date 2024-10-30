package com.datn.endless.services;

import com.datn.endless.dtos.VersionAttributeDTO2;
import com.datn.endless.entities.Attributevalue;
import com.datn.endless.entities.Productversion;
import com.datn.endless.entities.Versionattribute;
import com.datn.endless.models.VersionAttributeModel;
import com.datn.endless.repositories.AttributevalueRepository;
import com.datn.endless.repositories.ProductversionRepository;
import com.datn.endless.repositories.VersionattributeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class VersionAttributeService {

    @Autowired
    private VersionattributeRepository versionAttributeRepository;

    @Autowired
    private ProductversionRepository productVersionRepository;

    @Autowired
    private AttributevalueRepository attributeValueRepository;

    @Transactional
    public List<VersionAttributeDTO2> createVersionAttributes(VersionAttributeModel model) {
        // Validate the input model
        validateVersionAttributeModel(model);

        // Retrieve the associated ProductVersion entity
        Productversion productVersion = productVersionRepository.findById(model.getProductVersionID())
                .orElseThrow(() -> new IllegalArgumentException("ProductVersion không tồn tại."));

        // Lấy tất cả các Versionattribute liên quan đến ProductVersion này
        List<Versionattribute> existingAttributes =
                versionAttributeRepository.findByProductVersionID(productVersion);

        // Tạo danh sách DTO để trả về
        List<VersionAttributeDTO2> dtos = new ArrayList<>();

        // Tạo một Set để theo dõi các AttributeID đã được thêm
        Set<String> addedAttributeIDs = new HashSet<>();

        // Duyệt qua từng AttributeValueID trong yêu cầu
        for (String attributeValueID : model.getAttributeValueIDs()) {
            // Retrieve the corresponding Attributevalue entity
            Attributevalue attributeValue = attributeValueRepository.findById(attributeValueID)
                    .orElseThrow(() -> new IllegalArgumentException("AttributeValue không tồn tại."));

            // Lấy AttributeID của giá trị thuộc tính hiện tại
            String currentAttributeID = attributeValue.getAttribute().getAttributeID();

            // Kiểm tra xem đã có giá trị cho Attribute này chưa
            if (addedAttributeIDs.contains(currentAttributeID)) {
                throw new IllegalArgumentException("Không thể thêm nhiều giá trị cho AttributeName: "
                        + attributeValue.getAttribute().getAttributeID());
            }

            // Kiểm tra xem ProductVersion đã có giá trị từ AttributeName này chưa
            boolean isDuplicate = existingAttributes.stream()
                    .anyMatch(attr -> attr.getAttributeValueID().getAttribute().getAttributeID()
                            .equals(currentAttributeID));

            if (isDuplicate) {
                throw new IllegalArgumentException("ProductVersion đã có giá trị từ AttributeName: "
                        + attributeValue.getAttribute().getAttributeID());
            }

            // Thêm AttributeID vào Set để theo dõi
            addedAttributeIDs.add(currentAttributeID);

            // Tạo mới VersionAttribute
            Versionattribute versionAttribute = new Versionattribute();
            versionAttribute.setProductVersionID(productVersion);
            versionAttribute.setAttributeValueID(attributeValue);

            // Lưu VersionAttribute vào database
            versionAttributeRepository.save(versionAttribute);

            // Tạo DTO cho VersionAttribute mới
            VersionAttributeDTO2 dto = new VersionAttributeDTO2();
            dto.setVersionAttributeID(versionAttribute.getVersionAttributeID());
            dto.setProductVersionID(model.getProductVersionID());
            dto.setAttributeValueIDs(model.getAttributeValueIDs());

            // Thêm DTO vào danh sách kết quả
            dtos.add(dto);
        }

        // Trả về danh sách các DTO
        return dtos;
    }



    @Transactional
    public List<VersionAttributeDTO2> updateVersionAttributes(String productVersionID, List<String> newAttributeValueIDs) {
        // Kiểm tra sự tồn tại của ProductVersion
        Productversion productVersion = productVersionRepository.findById(productVersionID)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy ProductVersion với ID: " + productVersionID));

        // Lấy tất cả các Versionattribute liên quan đến ProductVersion này
        List<Versionattribute> existingAttributes = versionAttributeRepository.findByProductVersionID(productVersion);

        // Xóa các bản ghi VersionAttribute cũ (nếu cần thay thế hoàn toàn)
        versionAttributeRepository.deleteAll(existingAttributes);

        // Tạo danh sách DTO để trả về
        List<VersionAttributeDTO2> updatedDTOs = new ArrayList<>();

        // Tạo một Set để theo dõi các AttributeID đã được thêm
        Set<String> addedAttributeIDs = new HashSet<>();

        // Thêm các AttributeValueID mới
        for (String attributeValueID : newAttributeValueIDs) {
            Attributevalue attributeValue = attributeValueRepository.findById(attributeValueID)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy AttributeValue với ID: " + attributeValueID));

            // Lấy AttributeID của giá trị thuộc tính hiện tại
            String currentAttributeID = attributeValue.getAttribute().getAttributeID();

            // Kiểm tra xem đã có giá trị cho Attribute này chưa
            if (addedAttributeIDs.contains(currentAttributeID)) {
                throw new IllegalArgumentException("Không thể thêm nhiều giá trị cho AttributeName: "
                        + attributeValue.getAttribute().getAttributeID());
            }

            // Kiểm tra xem ProductVersion đã có giá trị từ AttributeName này chưa
            boolean isDuplicate = existingAttributes.stream()
                    .anyMatch(attr -> attr.getAttributeValueID().getAttribute().getAttributeID()
                            .equals(currentAttributeID));

            if (isDuplicate) {
                throw new IllegalArgumentException("ProductVersion đã có giá trị từ AttributeName: "
                        + attributeValue.getAttribute().getAttributeID());
            }

            // Thêm AttributeID vào Set để theo dõi
            addedAttributeIDs.add(currentAttributeID);

            // Tạo mới từng Versionattribute
            Versionattribute newVersionAttribute = new Versionattribute();
            newVersionAttribute.setProductVersionID(productVersion);
            newVersionAttribute.setAttributeValueID(attributeValue);

            // Lưu vào database
            versionAttributeRepository.save(newVersionAttribute);

            // Tạo DTO cho từng Versionattribute mới
            VersionAttributeDTO2 dto = new VersionAttributeDTO2();
            dto.setVersionAttributeID(newVersionAttribute.getVersionAttributeID());
            dto.setProductVersionID(productVersionID);
            dto.setAttributeValueIDs(newAttributeValueIDs); // Lưu danh sách mới

            updatedDTOs.add(dto);
        }

        return updatedDTOs; // Trả về danh sách DTO đã cập nhật
    }


    public Optional<VersionAttributeDTO2> getVersionAttributeById(String id) {
        return versionAttributeRepository.findById(id).map(this::convertToDTO);
    }

    public List<VersionAttributeDTO2> getAllVersionAttributes() {
        List<Versionattribute> entities = versionAttributeRepository.findAll();

        return entities.stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Transactional
    public void deleteVersionAttribute(String id) {
        if (!versionAttributeRepository.existsById(id)) {
            throw new IllegalArgumentException("Không tìm thấy VersionAttribute có ID: " + id);
        }
        versionAttributeRepository.deleteById(id);
    }

    private VersionAttributeDTO2 convertToDTO(Versionattribute entity) {
        VersionAttributeDTO2 dto = new VersionAttributeDTO2();
        dto.setVersionAttributeID(entity.getVersionAttributeID());
        dto.setProductVersionID(entity.getProductVersion().getProductVersionID());

        // Lấy tất cả các AttributeValueID liên quan đến ProductVersion này
        List<String> attributeValueIDs = versionAttributeRepository
                .findByProductVersionID(entity.getProductVersion())
                .stream()
                .map(va -> va.getAttributeValue().getAttributeValueID())
                .collect(Collectors.toList());

        dto.setAttributeValueIDs(attributeValueIDs); // Set danh sách ID vào DTO
        return dto;
    }




    private void validateVersionAttributeModel(VersionAttributeModel model) {
        if (!StringUtils.hasText(model.getProductVersionID())) {
            throw new IllegalArgumentException("ID của ProductVersion không được để trống.");
        }
        if (model.getAttributeValueIDs() == null || model.getAttributeValueIDs().isEmpty()) {
            throw new IllegalArgumentException("Danh sách AttributeValueID không được để trống.");
        }
    }
}
