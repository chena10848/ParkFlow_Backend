package application_operation.ParkFlow.controller.parking.payload;

import application_operation.ParkFlow.enums.ParkingRequestEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ParkingRequestUpdateRq {

    @NotNull(message = "{id.notnull}")
    @Schema(title = "申請單編號", description = "申請單編號", example = "1")
    private Integer id;

    @NotNull(message = "{status.notnull}")
    @Schema(title = "審核結果", description = "審核結果", example = "APPROVED")
    private ParkingRequestEnum status;

    @Schema(title = "車位號碼", description = "車位號碼", example = "10")
    private Integer parkingSlotNumber;
}
