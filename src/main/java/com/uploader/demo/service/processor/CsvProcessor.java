package com.uploader.demo.service.processor;

import org.springframework.web.multipart.MultipartFile;

public interface CsvProcessor {
    void process(MultipartFile file);
}
