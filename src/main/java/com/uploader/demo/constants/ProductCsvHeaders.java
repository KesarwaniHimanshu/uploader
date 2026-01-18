package com.uploader.demo.constants;

import java.util.List;

public final class ProductCsvHeaders {

    public static final List<String> HEADERS = List.of(
            "Local Product ID",
            "Product Reference ID",
            "Company Name",
            "Product Name",
            "Product Description",
            "Link To Product Website",
            "Product Type",
            "Usage Model - Single or Multiple Users?",
            "Local Edition ID",
            "Edition Reference ID",
            "Edition Name",
            "Local Pricing Plan ID",
            "Edition Pricing Plan Reference ID",
            "Billing Frequency",
            "Flat Rate Price Amounts",
            "Flat Rate Price Currencies",
            "Price Per Unit - Unit Type",
            "Price Per Unit - Amounts",
            "Price Per Unit - Currencies",
            "Price Per Unit - Setup Fee Amounts",
            "Price Per Unit - Setup Fee Currencies",
            "Setup Fee Amounts",
            "Setup Fee Currencies",
            "Contract Terms - Minimum Contract Duration",
            "Contract Terms - Flat Contract Fee Amounts",
            "Contract Terms - Flat Contract Fee Currencies",
            "Contract Terms - Block Edition Upgrades Mid Contract?",
            "Contract Terms - Block Switching to Shorter Contracts?",
            "Contract Terms - Termination Fee Description",
            "Contract Terms - Termination Flat Fee Amounts",
            "Contract Terms - Termination Flat Fee Currencies"
    );

    private ProductCsvHeaders() {}
}
