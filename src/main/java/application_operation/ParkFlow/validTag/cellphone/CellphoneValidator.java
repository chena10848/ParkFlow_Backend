package application_operation.ParkFlow.validTag.cellphone;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CellphoneValidator implements ConstraintValidator<Cellphone, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // 交由 @NotBlank 來處理
        }
        return value.matches("^09\\d{8}$");
    }
}
