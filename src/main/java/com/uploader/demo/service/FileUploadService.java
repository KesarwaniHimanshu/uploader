package com.uploader.demo.service;

import com.uploader.demo.constants.EntityType;
import com.uploader.demo.model.GenericCsvDocument;
import com.uploader.demo.model.OriginalData;
import com.uploader.demo.repository.GenericCsvRepository;
import com.uploader.demo.repository.OriginalDataRepository;
import com.uploader.demo.service.processor.CsvProcessor;
import com.uploader.demo.service.processor.CsvProcessorFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
//public class FileUploadService {
//
//    private final GenericCsvRepository repository;
//
//    public void processCsv(MultipartFile file) {
//
//        try (BufferedReader br = new BufferedReader(
//                new InputStreamReader(file.getInputStream()))) {
//
//            String headerLine = br.readLine(); // first line = header
//            if (headerLine == null) {
//                throw new RuntimeException("CSV file is empty");
//            }
//
//            String[] headers = headerLine.split(",");
//
//            String line;
//            int count = 0;
//
//            while ((line = br.readLine()) != null) {
//
//                if (line.trim().isEmpty()) continue;
//
//                String[] values = line.split(",");
//
//                if (values.length != headers.length) {
//                    System.out.println("Skipping invalid row: " + line);
//                    continue;
//                }
//
//                Map<String, Object> rowData = new HashMap<>();
//
//                for (int i = 0; i < headers.length; i++) {
//                    rowData.put(headers[i].trim(), values[i].trim());
//                }
//
//                repository.save(new GenericCsvDocument(rowData));
//                count++;
//            }
//
//            System.out.println("Total records saved: " + count);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException("CSV processing failed");
//        }
//    }
//}
public class FileUploadService {

    private final CsvProcessorFactory factory;

    public void processCsv(MultipartFile file, EntityType entityType) {
        CsvProcessor processor = factory.getProcessor(entityType);
        processor.process(file);
    }
}