package application_operation.ParkFlow.controller.parking.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParkingQuotaCreateRq {

    @NotNull(message = "{startDate.notnull}")
    @Schema(title = "下週開始日期", example = "2025-03-17T00:00:00")
    private LocalDateTime startDate;

    @NotNull(message = "{totalSlots.notnull}")
    @Schema(title = "可申請的車位數量上限", example = "10")
    private Integer totalSlots;
}
