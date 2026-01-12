package com.uploader.demo.service.processor;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component("PRODUCT_BASICS")
@RequiredArgsConstructor
public class ProductBasicsCsvProcessor implements CsvProcessor {

    private final MongoTemplate mongoTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void process(MultipartFile file) {

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(file.getInputStream()))) {

            String headerLine = br.readLine();
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

                mongoTemplate.insert(data, "product_basics");
                kafkaTemplate.send("product-basics-topic", data);
            }

        } catch (Exception e) {
            throw new RuntimeException("PRODUCT_BASICS CSV failed", e);
        }
    }
}
