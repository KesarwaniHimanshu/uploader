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
public class EditionCsvProcessor implements CsvProcessor {

    private final MongoTemplate mongoTemplate;

    @Override
    public EntityType getEntityType() {
        return EntityType.EDITION;
    }

    @Override
    public ProcessType getProcessType() {
        return ProcessType.UPDATE;
    }

    private static final List<String> EXPECTED_HEADERS = List.of(
            "Product Reference ID",
            "Product Name",
            "Edition Reference ID",
            "Edition Name"
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

                // ---------------------------------
                // 1. Save RAW row (audit collection)
                // ---------------------------------

                Map<String, Object> raw = new LinkedHashMap<>();

                for (String h : EXPECTED_HEADERS) {
                    raw.put(normalize(h), record.get(h));
                }

                raw.put("processId", processId);
                raw.put("entity", getEntityType().name());
                raw.put("processType", getProcessType().name());
                raw.put("uploadedAt", Instant.now());

                mongoTemplate.insert(raw, "product_editions");


                // ---------------------------------
                // 2. Update PRODUCT DB (edition only)
                // ---------------------------------

                String productRefId = record.get("Product Reference ID").trim();
                String editionRefId = record.get("Edition Reference ID").trim();
                String newEditionName = record.get("Edition Name").trim();

                Query query = new Query(
                        Criteria.where("product.productReferenceId").is(productRefId)
                                .and("product.editions.editionReferenceId").is(editionRefId)
                );

                Update update = new Update()
                        .set("product.editions.$.editionName", newEditionName)
                        .set("updatedAt", Instant.now());

                var result = mongoTemplate.updateFirst(query, update, "product");

                // ---------------------------------
                // 3. Update audit row with result
                // ---------------------------------

                Query rawQuery = new Query(
                        Criteria.where("_id").is(raw.get("_id"))
                );

                Update rawUpdate = new Update()
                        .set("updateStatus", result.getMatchedCount() == 0 ? "FAILED" : "UPDATED");

                if (result.getMatchedCount() == 0) {
                    rawUpdate.set("errorMessage",
                            "No matching product/edition found for ProductRefId="
                                    + productRefId + " EditionRefId=" + editionRefId);
                }

                mongoTemplate.updateFirst(rawQuery, rawUpdate, "product_editions");
            }

        } catch (Exception e) {
            throw new RuntimeException("EDITION UPDATE CSV failed", e);
        }
    }

    // ----------------- helpers -----------------

    private void validateHeaders(Set<String> actual) {

        List<String> incoming = actual.stream().map(String::trim).toList();

        if (incoming.size() != EXPECTED_HEADERS.size()) {
            throw new RuntimeException("Invalid column count in EDITION CSV");
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
