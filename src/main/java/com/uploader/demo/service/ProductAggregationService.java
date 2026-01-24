package com.uploader.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static com.uploader.demo.util.MongoValueHelper.*;

@Service
@RequiredArgsConstructor
public class ProductAggregationService {

    private final MongoTemplate mongoTemplate;

    // ================= ENTRY =================

    public void buildFinalProduct(String processId) {
        System.out.println("Building final product for processId=" + processId);

        Query query = new Query(Criteria.where("processId").is(processId));
        List<Map> rows = mongoTemplate.find(query, Map.class, "product_create");

        if (rows.isEmpty()) {
            throw new RuntimeException("No data found for processId: " + processId);
        }

        Map<String, List<Map>> productGroups =
                rows.stream()
                        .filter(r -> r.get("local_product_id") != null)
                        .collect(Collectors.groupingBy(r -> r.get("local_product_id").toString()));

        for (String localProductId : productGroups.keySet()) {
            Map<String, Object> product =
                    buildProduct(localProductId, productGroups.get(localProductId));

            Map<String, Object> finalDoc = new LinkedHashMap<>();
            finalDoc.put("processId", processId);
            finalDoc.put("createdAt", Instant.now());
            finalDoc.put("updatedAt", Instant.now());
            finalDoc.put("product", product);

            mongoTemplate.insert(finalDoc, "product");

        }
    }

    // ================= PRODUCT =================

    private Map<String, Object> buildProduct(String localProductId,
                                             List<Map> rows) {

        Map first = rows.get(0);

        Map<String, Object> product = new LinkedHashMap<>();

        product.put("localProductId", localProductId);
        product.put("productReferenceId", UUID.randomUUID().toString());
        product.put("companyName", first.get("company_name"));
        product.put("productName", first.get("product_name"));
        product.put("productDescription", first.get("product_description"));
        product.put("productWebsite", first.get("link_to_product_website"));
        product.put("productType", first.get("product_type"));
        product.put("usageModel", getString(first, "usage_model_single_or_multiple_users"));

        product.put("editions", buildEditions(rows));

        return product;
    }


    // ================= EDITION =================

    private List<Map<String, Object>> buildEditions(List<Map> rows) {

        Map<String, List<Map>> editionGroups =
                rows.stream()
                        .filter(r -> r.get("local_edition_id") != null)
                        .collect(Collectors.groupingBy(r -> r.get("local_edition_id").toString()));

        List<Map<String, Object>> editions = new ArrayList<>();

        for (String localEditionId : editionGroups.keySet()) {

            List<Map> editionRows = editionGroups.get(localEditionId);
            Map first = editionRows.get(0);

            Map<String, Object> edition = new LinkedHashMap<>();

            edition.put("localEditionId", localEditionId);
            edition.put("editionReferenceId", UUID.randomUUID().toString());
            edition.put("editionName", first.get("edition_name"));
            edition.put("pricingPlans", buildPricingPlans(editionRows));

            editions.add(edition);
        }

        return editions;
    }

    // ================= PRICING PLAN =================

    private List<Map<String, Object>> buildPricingPlans(List<Map> rows) {

        Map<String, List<Map>> pricingGroups =
                rows.stream()
                        .filter(r -> r.get("local_pricing_plan_id") != null)
                        .collect(Collectors.groupingBy(r -> r.get("local_pricing_plan_id").toString()));

        List<Map<String, Object>> pricingPlans = new ArrayList<>();

        for (String localPricingPlanId : pricingGroups.keySet()) {

            Map r = pricingGroups.get(localPricingPlanId).get(0);

            Map<String, Object> pricingPlan = new LinkedHashMap<>();

            pricingPlan.put("localPricingPlanId", localPricingPlanId);
            pricingPlan.put("pricingPlanReferenceId", UUID.randomUUID().toString());
            pricingPlan.put("billingFrequency", r.get("billing_frequency"));

            pricingPlan.put("flatRate", buildMoney(
                    r.get("flat_rate_price_amounts"),
                    r.get("flat_rate_price_currencies")
            ));

            pricingPlan.put("pricePerUnit", buildPricePerUnit(r));

            pricingPlan.put("setupFee", buildMoney(
                    r.get("setup_fee_amounts"),
                    r.get("setup_fee_currencies")
            ));

            pricingPlan.put("contractTerms", buildContractTerms(r));

            pricingPlans.add(pricingPlan);
        }

        return pricingPlans;
    }

    // ================= HELPERS =================

    private Map<String, Object> buildMoney(Object amounts, Object currencies) {

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("amounts", split(amounts));
        map.put("currencies", split(currencies));
        return map;
    }

    private Map<String, Object> buildPricePerUnit(Map r) {

        return Map.of(
                "unitType", getString(r, "price_per_unit_unit_type"),
                "amounts", split(r.get("price_per_unit_amounts")),
                "currencies", split(r.get("price_per_unit_currencies")),
                "setupFeeAmounts", split(r.get("price_per_unit_setup_fee_amounts")),
                "setupFeeCurrencies", split(r.get("price_per_unit_setup_fee_currencies"))
        );
    }


    private Map<String, Object> buildContractTerms(Map r) {

        return Map.of(
                "minimumContractDuration", getInteger(r, "contract_terms_minimum_contract_duration"),

                "flatContractFee", buildMoney(
                        r.get("contract_terms_flat_contract_fee_amounts"),
                        r.get("contract_terms_flat_contract_fee_currencies")
                ),

                "blockEditionUpgradesMidContract",
                getBoolean(r, "contract_terms_block_edition_upgrades_mid_contract"),

                "blockSwitchingToShorterContracts",
                getBoolean(r, "contract_terms_block_switching_to_shorter_contracts"),

                "terminationFeeDescription",
                getString(r, "contract_terms_termination_fee_description"),

                "terminationFlatFee", buildMoney(
                        r.get("contract_terms_termination_flat_fee_amounts"),
                        r.get("contract_terms_termination_flat_fee_currencies")
                )
        );
    }


    // ================= UTIL =================

    private List<String> split(Object value) {
        if (value == null) return List.of();
        String s = value.toString().trim();
        if (s.isEmpty()) return List.of();

        return Arrays.stream(s.split(","))
                .map(String::trim)
                .filter(v -> !v.isEmpty())
                .toList();
    }

    private Integer toInt(Object val) {
        if (val == null) return null;
        try {
            return Integer.parseInt(val.toString().trim());
        } catch (Exception e) {
            return null;
        }
    }

    private Boolean toBoolean(Object val) {
        if (val == null) return null;
        return Boolean.parseBoolean(val.toString().trim());
    }
}
