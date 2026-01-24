package com.uploader.demo.controller;

import com.uploader.demo.constants.EntityType;
import com.uploader.demo.constants.ProcessType;
import com.uploader.demo.dto.FileUploadRequest;
import com.uploader.demo.service.FileUploadService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;

import java.util.Map;
import java.util.UUID;

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
    public ResponseEntity<Map<String, String>>  uploadCsv(
            @RequestPart("file") MultipartFile file,
            @RequestPart("entity") String entity,
            @RequestParam("processType") ProcessType processType)
//            @Valid @RequestPart("data") FileUploadRequest request)

    {
        EntityType entityType = EntityType.valueOf(entity);
        System.out.println("/uplod api called+++++++++++++++++++++++");
        String processId =  fileUploadService.processCsv(file, entityType, processType);
        return ResponseEntity.ok(Map.of(
                "message", "File processed successfully",
                "processId", processId
        ));
    }
}