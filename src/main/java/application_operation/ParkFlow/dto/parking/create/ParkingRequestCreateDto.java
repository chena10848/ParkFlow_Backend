package application_operation.ParkFlow.dto.parking.create;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ParkingRequestCreateDto {
    private LocalDateTime startDate;
    private String cellPhone;
    private String carNumber;
    private String carType;
}
