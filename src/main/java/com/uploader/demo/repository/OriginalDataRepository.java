package com.uploader.demo.repository;
import com.uploader.demo.model.OriginalData;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OriginalDataRepository
        extends MongoRepository<OriginalData, String> {
}
