package com.projektsse.backend.interfaces;

import com.nulabinc.zxcvbn.Zxcvbn;
import com.projektsse.backend.controller.dto.RegisterReq;
import com.projektsse.backend.exceptions.WeakPasswordException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

import static com.projektsse.backend.exceptions.GlobalExceptionHandler.log;

/**
 * This class defines die password policy for the application.
 * It ensures that the password provided by the user is strong enough to protect their account.
 * It validates the password strength using the zxcvbn library.
 * The password must have a score of at least 4 to be considered strong.
 * Implementing this class as a ConstraintValidator allows us to use it as a custom validation annotation on the password field in our DTOs.
 */
public class StrongPasswordEmailCheckValidator implements ConstraintValidator<StrongPasswordEmailCheck, RegisterReq> {

    /**
     * This method checks if the provided password meets the requirements of the password policy.
     * @param req the RegisterReq object (DTO) containing the password (+ email) to be validated
     * @param context the context in which the constraint is evaluated
     * @return boolean indicating whether the password is valid or not
     * @throws WeakPasswordException if the password is null, too long, or does not meet the strength requirements
     */
    @Override
    public boolean isValid(RegisterReq req, ConstraintValidatorContext context) {
        if (req.password() == null) throw new WeakPasswordException("Password cannot be empty.");

        if (req.password().length() >= 255) {
            throw new WeakPasswordException("Password must be less than 255 characters.");
        }

        String email = req.email();
        String localPart = email.substring(0, email.indexOf('@'));
        String domainPart = email.substring(email.indexOf('@') + 1);
        log.info("Validated email '{}': local part = {}, domain part = {}", email, localPart, domainPart);

        List<String> userInputs = List.of(email, localPart, domainPart);

        Zxcvbn zxcvbn = new Zxcvbn();
        int score = zxcvbn.measure(req.password(), userInputs).getScore();

        if (score < 4) {
            throw new WeakPasswordException("Password is too weak.");
        }
        return true;
    }
}
