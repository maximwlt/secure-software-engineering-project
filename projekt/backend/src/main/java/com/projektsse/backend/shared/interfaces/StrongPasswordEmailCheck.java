package com.projektsse.backend.shared.interfaces;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = StrongPasswordEmailCheckValidator.class)
@Documented
public @interface StrongPasswordEmailCheck {
    String message() default "Password is too weak.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}