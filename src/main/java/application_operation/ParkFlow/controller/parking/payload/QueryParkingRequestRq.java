package application_operation.ParkFlow.controller.parking.payload;

import io.swagger.v3.oas.annotations.media.Schema;
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
public class QueryParkingRequestRq {

    @NotNull(message = "{startDate.notnull}")
    @Schema(title = "當周開始時間", description = "當周開始時間", example = "2025-03-09T00:00:00")
    private LocalDateTime startDate;
}
