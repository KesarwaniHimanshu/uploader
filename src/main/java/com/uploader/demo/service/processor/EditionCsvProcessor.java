package com.uploader.demo.service.processor;

import com.uploader.demo.constants.EntityType;
import com.uploader.demo.constants.ProcessType;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class EditionCsvProcessor implements CsvProcessor {

//    private final MongoTemplate mongoTemplate;

    @Override
    public EntityType getEntityType() {
        return EntityType.EDITION;
    }

    @Override
    public ProcessType getProcessType() {
        return ProcessType.UPDATE;
    }
    public void process(MultipartFile file, String processId) {
        // parse edition csv
    }
}

