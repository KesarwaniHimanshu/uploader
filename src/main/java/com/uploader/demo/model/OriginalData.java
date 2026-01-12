package com.uploader.demo.model;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "originalDataSource")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OriginalData {

    private String id;
    private String name;
    private String email;
    private Integer age;
}
