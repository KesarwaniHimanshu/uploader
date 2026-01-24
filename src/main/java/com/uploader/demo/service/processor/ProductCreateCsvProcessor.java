package com.uploader.demo.service.processor;

import com.uploader.demo.constants.EntityType;
import com.uploader.demo.constants.ProcessType;
import com.uploader.demo.constants.ProductCsvHeaders;
import com.uploader.demo.service.ProductAggregationService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.coyote.BadRequestException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.Instant;
import java.util.*;

import static com.uploader.demo.util.MongoValueHelper.normalize;

@Component
@RequiredArgsConstructor
public class ProductCreateCsvProcessor implements CsvProcessor {

    private final MongoTemplate mongoTemplate;
    private final ProductAggregationService productAggregationService;

    @Override
    public EntityType getEntityType() {
        return EntityType.PRODUCT;
    }

    @Override
    public ProcessType getProcessType() {
        return ProcessType.CREATE;
    }

    private void validateHeaders(List<String> headers) {

        List<String> expected = ProductCsvHeaders.HEADERS;

        if (headers.size() != expected.size()) {
            throw new RuntimeException("Column count mismatch. Expected "
                    + expected.size() + " but found " + headers.size());
        }

        for (int i = 0; i < headers.size(); i++) {
            String actual = headers.get(i).replace("\uFEFF", "").trim();
            String exp = expected.get(i).trim();

            if (!actual.equalsIgnoreCase(exp)) {
                throw new RuntimeException(
                        "Invalid column at position " + (i + 1)
                                + ". Expected [" + exp + "] but found [" + actual + "]"
                );
            }
        }
    }

    @Override
    public void process(MultipartFile file, String processId) {

        saveRawRows(file, processId);

        // ✅ only product-create processor triggers final build
        System.out.println("Rows saved, starting product aggregation for processId=" + processId);
        productAggregationService.buildFinalProduct(processId);
    }

    private void saveRawRows(MultipartFile file, String processId) {
        try (
                Reader reader = new InputStreamReader(file.getInputStream());
                CSVParser csvParser = CSVFormat.DEFAULT
                        .withFirstRecordAsHeader()
                        .withTrim()
                        .parse(reader)
        ) {

            Map<String, Integer> actualHeaders = csvParser.getHeaderMap();
            List<String> headers = new ArrayList<>(actualHeaders.keySet());

            validateHeaders(headers);

            for (CSVRecord record : csvParser) {

                Map<String, Object> document = new LinkedHashMap<>();

                for (String header : headers) {
                    document.put(normalize(header), record.get(header));
                }

                document.put("processId", processId);
                document.put("entity", getEntityType().name());
                document.put("processType", getProcessType().name());
                document.put("uploadedAt", Instant.now());

                mongoTemplate.insert(document, "product_create");
            }

            // existing CSV logic (unchanged)
        } catch (Exception e) {
            throw new RuntimeException("PRODUCT CREATE CSV failed", e);
        }
    }

}
