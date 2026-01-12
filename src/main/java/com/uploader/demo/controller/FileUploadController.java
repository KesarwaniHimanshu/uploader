package com.uploader.demo.controller;

import com.uploader.demo.constants.EntityType;
import com.uploader.demo.service.FileUploadService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileUploadController {

    private final FileUploadService fileUploadService;

    @PostConstruct
    public void init() {
        System.out.println("FileUploadController loaded");
    }

    @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadCsv(
            @RequestPart("file") MultipartFile file,
            @RequestPart("entity") String entity) {
        EntityType entityType = EntityType.valueOf(entity);
        System.out.println("/uplod api called+++++++++++++++++++++++");
        fileUploadService.processCsv(file, entityType);
        return ResponseEntity.ok("File processed for " + entityType);
    }
}