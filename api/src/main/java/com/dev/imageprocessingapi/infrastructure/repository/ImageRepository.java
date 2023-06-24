package com.dev.imageprocessingapi.infrastructure.repository;

import com.dev.imageprocessingapi.model.Image;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends MongoRepository<Image, String> {
}
