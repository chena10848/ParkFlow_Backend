package application_operation.ParkFlow.controller.parking.payload;

import application_operation.ParkFlow.validTag.cellphone.Cellphone;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ParkingRequestCreateRq {

    @NotNull(message = "{startDate.notnull}")
    @Schema(title = "下周開始時間", description = "下周開始時間", example = "2025-03-02T00:00:00")
    private LocalDateTime startDate;

    @Cellphone
    @NotBlank(message = "{cellphone.notblank}")
    @Schema(title = "電話號碼", description = "電話號碼")
    private String cellPhone;

    @NotBlank(message = "{carNumber.notblank}")
    @Schema(title = "車牌號碼", description = "車牌號碼", example = "EAF-2200")
    private String carNumber;

    @NotBlank(message = "{carType.notblank}")
    @Schema(title = "車子品牌", description = "車子品牌", example = "TOYOTA")
    private String carType;
}
