package com.uploader.demo.service.processor;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component("EDITION")
public class EditionCsvProcessor implements CsvProcessor {
    public void process(MultipartFile file, String processId) {
        // parse edition csv
    }
}

