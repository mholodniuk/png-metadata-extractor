package com.dev.imageprocessingapi.validation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = MongoObjectIdValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
public @interface MongoObjectId {
    String message() default "Invalid ObjectId format";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
