package com.projektsse.backend.interfaces;

import com.nulabinc.zxcvbn.Zxcvbn;
import jakarta.validation.ConstraintValidator;

public class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {
    @Override
    public boolean isValid(String password, jakarta.validation.ConstraintValidatorContext context) {
        if (password == null) {
            return false;
        }
        Zxcvbn zxcvbn = new Zxcvbn();
        int score = zxcvbn.measure(password).getScore();

        return score == 4;
    }
}
