package application_operation.ParkFlow.validTag.englishname;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EnglishNameValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface EnglishName {
    String DEFAULT_MESSAGE = "輸入格式錯誤：英文姓名欄位只能填入英文、空格和連接線。";

    String message() default DEFAULT_MESSAGE;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
