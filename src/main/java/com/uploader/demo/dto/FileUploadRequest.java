package com.uploader.demo.dto;

import com.uploader.demo.constants.EntityType;
import com.uploader.demo.constants.ProcessType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;
@Getter
@Setter
@Data
public class FileUploadRequest {

    @NotNull(message = "entity is required")
    private EntityType entity;

    @NotNull(message = "processType is required")
    private ProcessType processType;
}
