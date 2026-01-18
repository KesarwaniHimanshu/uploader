package com.uploader.demo.service;

import com.uploader.demo.constants.EntityType;
import com.uploader.demo.constants.ProcessType;
import com.uploader.demo.constants.ProcessorKey;
import com.uploader.demo.model.GenericCsvDocument;
import com.uploader.demo.model.OriginalData;
import com.uploader.demo.repository.GenericCsvRepository;
import com.uploader.demo.repository.OriginalDataRepository;
import com.uploader.demo.service.processor.CsvProcessor;
import com.uploader.demo.service.processor.CsvProcessorFactory;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

@Service
public class FileUploadService {

    private final Map<ProcessorKey, CsvProcessor> processorMap = new HashMap<>();

    public FileUploadService(List<CsvProcessor> processors) {
        for (CsvProcessor p : processors) {
            processorMap.put(
                    new ProcessorKey(p.getEntityType(), p.getProcessType()),
                    p
            );
        }
    }

    public String processCsv(MultipartFile file,
                             EntityType entity,
                             ProcessType processType) {

        String processId = UUID.randomUUID().toString();

        ProcessorKey key = new ProcessorKey(entity, processType);
        CsvProcessor processor = processorMap.get(key);

        if (processor == null) {
            throw new RuntimeException(
                    "No processor found for " + entity + " + " + processType
            );
        }

        processor.process(file, processId);

        return processId;
    }
}
