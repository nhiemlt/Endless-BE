package com.datn.endless.controllers;

import com.datn.endless.dtos.VersionAttributeDTO2;
import com.datn.endless.models.VersionAttributeModel;
import com.datn.endless.services.VersionAttributeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/version-attributes")
public class VersionAttributeController {

    @Autowired
    private VersionAttributeService versionAttributeService;

    // Tạo mới VersionAttribute
    @PostMapping
    public ResponseEntity<List<VersionAttributeDTO2>> createVersionAttributes(
            @Validated @RequestBody VersionAttributeModel model) {
        List<VersionAttributeDTO2> dtos = versionAttributeService.createVersionAttributes(model);
        return new ResponseEntity<>(dtos, HttpStatus.CREATED);
    }

    // Cập nhật danh sách VersionAttributes theo productVersionID
    @PutMapping("/{productVersionID}")
    public ResponseEntity<List<VersionAttributeDTO2>> updateVersionAttributes(
            @PathVariable String productVersionID,
            @RequestBody List<String> newAttributeValueIDs) {
        List<VersionAttributeDTO2> updatedAttributes =
                versionAttributeService.updateVersionAttributes(productVersionID, newAttributeValueIDs);
        return new ResponseEntity<>(updatedAttributes, HttpStatus.OK);
    }

    // Lấy VersionAttribute theo ID
    @GetMapping("/{id}")
    public ResponseEntity<VersionAttributeDTO2> getVersionAttributeById(@PathVariable String id) {
        Optional<VersionAttributeDTO2> attribute = versionAttributeService.getVersionAttributeById(id);
        return attribute.map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Lấy tất cả VersionAttributes
    @GetMapping
    public ResponseEntity<List<VersionAttributeDTO2>> getAllVersionAttributes() {
        List<VersionAttributeDTO2> attributes = versionAttributeService.getAllVersionAttributes();
        return new ResponseEntity<>(attributes, HttpStatus.OK);
    }

    // Xóa VersionAttribute theo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteVersionAttribute(@PathVariable String id) {
        versionAttributeService.deleteVersionAttribute(id);
        return ResponseEntity.ok("Xóa VersionAttribute thành công");
    }

}
