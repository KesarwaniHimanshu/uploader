package com.uploader.demo.service.processor;

import com.uploader.demo.constants.EntityType;
import com.uploader.demo.constants.ProcessType;
import org.springframework.web.multipart.MultipartFile;

public interface CsvProcessor {
    EntityType getEntityType();
    ProcessType getProcessType();
    void process(MultipartFile file, String processId);
}
