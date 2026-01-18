package com.uploader.demo.service.processor;

import com.uploader.demo.constants.EntityType;
import com.uploader.demo.constants.ProcessType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ProductBasicsCsvProcessor implements CsvProcessor {

    private final MongoTemplate mongoTemplate;

    @Override
    public EntityType getEntityType() {
        return EntityType.PRODUCT_BASICS;
    }

    @Override
    public ProcessType getProcessType() {
        return ProcessType.UPDATE;
    }

    @Override
    public void process(MultipartFile file, String processId) {

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(file.getInputStream()))) {

            String headerLine = br.readLine();

            if (headerLine == null) {
                throw new RuntimeException("Empty CSV file");
            }

            String[] headers = headerLine.split(",");

            if (!Arrays.equals(headers, new String[]{"productId","name","category"})) {
                throw new RuntimeException("Invalid PRODUCT_BASICS CSV format");
            }

            String line;
            while ((line = br.readLine()) != null) {

                String[] values = line.split(",");

                Map<String,Object> data = new HashMap<>();
                for (int i = 0; i < headers.length; i++) {
                    data.put(headers[i], values[i]);
                }

                data.put("processId", processId);
                data.put("entity", "PRODUCT_BASICS");
                data.put("processType", "UPDATE");

                mongoTemplate.insert(data, "product_basics");
            }

        } catch (Exception e) {
            throw new RuntimeException("PRODUCT_BASICS CSV failed", e);
        }
    }
}
