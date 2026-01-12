package com.uploader.demo.repository;

import com.uploader.demo.model.GenericCsvDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GenericCsvRepository
        extends MongoRepository<GenericCsvDocument, String> {
}

