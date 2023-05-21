package com.dev.imageprocessingapi.model;

import lombok.Data;
import lombok.ToString;
import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.time.LocalDateTime;

@Data
@Document(collection = "images")
public class Image {
    @Id
    @Field(targetType = FieldType.OBJECT_ID)
    private String id;
    private String fileType;
    private String fileName;
    @Indexed(expireAfterSeconds = 600)
    private LocalDateTime createdAt;
    @ToString.Exclude
    private Binary bytes;
    @ToString.Exclude
    private Binary magnitude;
    @ToString.Exclude
    private Binary phase;
    @ToString.Exclude
    private Binary hash;
}
