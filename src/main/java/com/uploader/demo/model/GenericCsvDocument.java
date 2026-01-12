package com.uploader.demo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Document(collection = "originalDataSource")
public class GenericCsvDocument {

    @Id
    private String id;

    private Map<String, Object> data;

    public GenericCsvDocument(Map<String, Object> data) {
        this.data = data;
    }

    public Map<String, Object> getData() {
        return data;
    }
}
