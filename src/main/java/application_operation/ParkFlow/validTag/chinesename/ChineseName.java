package application_operation.ParkFlow.validTag.chinesename;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ ElementType.FIELD, ElementType.PARAMETER }) // 可用於欄位或方法參數
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ChineseNameValidator.class) // 指定驗證邏輯
public @interface ChineseName {
    String DEFAULT_MESSAGE = "輸入格式錯誤：中文姓名欄位只能填入中文。";

    String message() default DEFAULT_MESSAGE;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
