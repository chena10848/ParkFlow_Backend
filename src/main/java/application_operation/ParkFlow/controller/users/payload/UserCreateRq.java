package application_operation.ParkFlow.controller.users.payload;

import application_operation.ParkFlow.validTag.cellphone.Cellphone;
import application_operation.ParkFlow.validTag.chinesename.ChineseName;
import application_operation.ParkFlow.validTag.englishname.EnglishName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRq {

    @ChineseName
    @Size(max = 20, min = 1, message = "{chineseName.long}")
    @NotBlank(message = "{chineseName.notblank}")
    @Schema(title = "中文姓名", example = "小吳")
    private String chineseName;

    @EnglishName
    @Size(max = 50, min = 1, message = "{englishName.long}")
    @NotBlank(message = "{englishName.notblank}")
    @Schema(title = "英文姓名", example = "Wu")
    private String englishName;

    @Email(message = "{email.format}")
    @NotBlank(message = "{email.notblank}")
    @Schema(title = "信箱", example = "wu@gmail.com")
    private String email;

    @Cellphone
    @NotBlank(message = "{cellphone.notblank}")
    @Schema(title = "電話號碼", example = "0912567456")
    private String cellphone;

    @NotBlank(message = "{carNumber.notblank}")
    @Schema(title = "車牌號碼", example = "DAO-3458")
    private String carNumber;

    @NotBlank(message = "{carType.notblank}")
    @Schema(title = "車型", example = "TOYOTA")
    private String carType;
}
