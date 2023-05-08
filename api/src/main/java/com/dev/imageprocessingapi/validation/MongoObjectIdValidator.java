package com.dev.imageprocessingapi.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.bson.types.ObjectId;

public class MongoObjectIdValidator implements ConstraintValidator<MongoObjectId, String> {
    @Override
    public void initialize(MongoObjectId constraintAnnotation) {
    }

    @Override
    public boolean isValid(String id, ConstraintValidatorContext context) {
        return ObjectId.isValid(id);
    }
}
