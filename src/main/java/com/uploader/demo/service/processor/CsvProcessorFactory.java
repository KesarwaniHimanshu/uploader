package com.uploader.demo.service.processor;

import com.uploader.demo.constants.EntityType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CsvProcessorFactory {

    private final Map<String, CsvProcessor> processorMap;

    public CsvProcessor getProcessor(EntityType entityType) {
        CsvProcessor processor = processorMap.get(entityType.name());
        if (processor == null)
            throw new RuntimeException("Unsupported entity type");
        return processor;
    }
}

