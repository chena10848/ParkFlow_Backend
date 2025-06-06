package application_operation.ParkFlow.dto.parking.update;

import application_operation.ParkFlow.enums.ParkingRequestEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateParkingRequestDto {
    private Integer id;
    private ParkingRequestEnum status;
    private Integer parkingSlotNumber;
}
