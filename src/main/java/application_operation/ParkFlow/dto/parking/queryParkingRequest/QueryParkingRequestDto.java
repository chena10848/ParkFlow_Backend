package application_operation.ParkFlow.dto.parking.queryParkingRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class QueryParkingRequestDto {
    private LocalDateTime startDate;
}
