package com.projektsse.backend.interfaces;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = StrongPasswordValidator.class)
@Documented
public @interface StrongPassword {
    String message() default "Passwort ist zu schwach";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}