package com.datn.endless.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link com.datn.endless.entities.Module}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModuleDTO implements Serializable {
    String moduleID;
    String moduleName;
    String description;
}