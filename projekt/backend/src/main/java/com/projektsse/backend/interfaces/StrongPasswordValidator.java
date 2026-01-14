package com.projektsse.backend.interfaces;

import com.nulabinc.zxcvbn.Zxcvbn;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {
    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return false;
        }
        if (password.length() >= 255) {
            return false;
        }
        Zxcvbn zxcvbn = new Zxcvbn();
        int score = zxcvbn.measure(password).getScore();

        return score == 4;
    }
}
