package com.uploader.demo.service.processor;

import com.uploader.demo.constants.ProcessType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component("PRICING_PLAN")
public class PricingPlanCsvProcessor implements CsvProcessor {
    public void process(MultipartFile file, ProcessType processType, String processId) {
        // parse pricing csv
    }
}

