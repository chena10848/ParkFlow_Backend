package application_operation.ParkFlow.controller.users.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginRq {

    @Email(message = "{email.format}")
    @NotBlank(message = "{email.notblank}")
    @Schema(title = "信箱", example = "lin@gmail.com")
    private String email;
}
