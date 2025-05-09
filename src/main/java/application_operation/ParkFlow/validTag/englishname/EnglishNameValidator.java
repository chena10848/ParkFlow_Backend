package application_operation.ParkFlow.validTag.englishname;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EnglishNameValidator implements ConstraintValidator<EnglishName, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // 交由 @NotBlank 來處理
        }
        return value.matches("^[A-Za-z\\- ]+$");
    }
}
