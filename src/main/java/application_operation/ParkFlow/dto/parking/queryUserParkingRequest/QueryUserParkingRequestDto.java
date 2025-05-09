package application_operation.ParkFlow.dto.parking.queryUserParkingRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class QueryUserParkingRequestDto {
    private LocalDateTime startDate;
}
