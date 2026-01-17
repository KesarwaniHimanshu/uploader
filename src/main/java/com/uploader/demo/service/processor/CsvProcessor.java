package com.uploader.demo.service.processor;

import com.uploader.demo.constants.ProcessType;
import org.springframework.web.multipart.MultipartFile;

public interface CsvProcessor {
    void process(MultipartFile file, ProcessType processType, String processId);
}
