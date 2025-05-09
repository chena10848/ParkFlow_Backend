package application_operation.ParkFlow.validTag.chinesename;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ChineseNameValidator implements ConstraintValidator<ChineseName, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // @NotBlank 會處理非空驗證
        }
        return value.matches("^\\p{IsHan}+$");
    }
}
