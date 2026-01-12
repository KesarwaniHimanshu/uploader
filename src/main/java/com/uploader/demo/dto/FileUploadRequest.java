package com.uploader.demo.dto;

import com.uploader.demo.constants.EntityType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileUploadRequest {
    private EntityType entity;
}
