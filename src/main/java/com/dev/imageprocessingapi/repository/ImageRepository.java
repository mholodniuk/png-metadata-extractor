package com.dev.imageprocessingapi.repository;

import com.dev.imageprocessingapi.model.Image;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ImageRepository extends MongoRepository<Image, String> {
}
