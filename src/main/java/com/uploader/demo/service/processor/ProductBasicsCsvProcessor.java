package com.uploader.demo.service.processor;

import com.uploader.demo.constants.EntityType;
import com.uploader.demo.constants.ProcessType;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.io.Reader;
import java.time.Instant;
import java.util.*;

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

    private static final List<String> EXPECTED_HEADERS = List.of(
            "Product Reference ID",
            "Company Name",
            "Product Name",
            "Product Description",
            "Link To Product Website",
            "Product Type",
            "Usage Model - Single or Multiple Users?"
    );


    @Override
    public void process(MultipartFile file, String processId) {

        try (
                Reader reader = new InputStreamReader(file.getInputStream());
                CSVParser parser = CSVFormat.DEFAULT
                        .withFirstRecordAsHeader()
                        .withTrim()
                        .parse(reader)
        ) {

            validateHeaders(parser.getHeaderMap().keySet());

            for (CSVRecord record : parser) {

                // -------------------------
                // 1. Build RAW row
                // -------------------------
                Map<String, Object> raw = new LinkedHashMap<>();

                for (String h : EXPECTED_HEADERS) {
                    raw.put(normalize(h), record.get(h));
                }

                raw.put("processId", processId);
                raw.put("entity", getEntityType().name());
                raw.put("processType", getProcessType().name());
                raw.put("uploadedAt", Instant.now());

                // -------------------------
                // 2. Update PRODUCT DB
                // -------------------------
                String productRefId = record.get("Product Reference ID").trim();

                Query query = new Query(
                        Criteria.where("product.productReferenceId").is(productRefId)
                );

                Update update = new Update()
                        .set("product.companyName", record.get("Company Name"))
                        .set("product.productName", record.get("Product Name"))
                        .set("product.productDescription", record.get("Product Description"))
                        .set("product.productWebsite", record.get("Link To Product Website"))
                        .set("product.productType", record.get("Product Type"))
                        .set("product.usageModel", record.get("Usage Model - Single or Multiple Users?"))
                        .set("updatedAt", Instant.now());

                var result = mongoTemplate.updateFirst(query, update, "product");

                // -------------------------
                // 3. Set audit result
                // -------------------------
                if (result.getMatchedCount() == 0) {
                    raw.put("updateStatus", "FAILED");
                    raw.put("errorMessage", "No product found for Product Reference ID: " + productRefId);
                } else {
                    raw.put("updateStatus", "UPDATED");
                }

                // -------------------------
                // 4. Save RAW + status
                // -------------------------
                mongoTemplate.insert(raw, "product_basics");
            }

        } catch (Exception e) {
            throw new RuntimeException("PRODUCT_BASICS UPDATE failed", e);
        }
    }


    // ----------------- helpers -----------------

    private void validateHeaders(Set<String> actual) {

        List<String> incoming = actual.stream().map(String::trim).toList();

        if (incoming.size() != EXPECTED_HEADERS.size()) {
            throw new RuntimeException("Invalid column count in PRODUCT_BASICS CSV");
        }

        for (String h : EXPECTED_HEADERS) {
            if (!incoming.contains(h)) {
                throw new RuntimeException("Missing required column: " + h);
            }
        }
    }

    private String normalize(String header) {
        return header
                .toLowerCase()
                .replace("?", "")
                .replace("-", "")
                .replace(" ", "_");
    }
}
