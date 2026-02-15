package com.honey.shop.domain.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Constraint(validatedBy = CustomPhone.class)
@Documented
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPhone {

    String message() default "Невалиден телефонски број";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
