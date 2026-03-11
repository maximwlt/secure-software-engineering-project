package com.projektsse.backend.interfaces;

import com.nulabinc.zxcvbn.Zxcvbn;
import com.projektsse.backend.exceptions.WeakPasswordException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {
    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null || password.isBlank()) return true; // @NotBlank will handle null and blank cases

        if (password.length() >= 255) {
            throw new WeakPasswordException("Password must be less than 255 characters.");
        }
        Zxcvbn zxcvbn = new Zxcvbn();
        int score = zxcvbn.measure(password).getScore();

        if (score < 4) {
            throw new WeakPasswordException("Password is too weak.");
        }
        return true; // Password meets all criteria
    }
}
