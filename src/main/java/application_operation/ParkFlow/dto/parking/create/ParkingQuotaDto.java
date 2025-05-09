package application_operation.ParkFlow.dto.parking.create;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ParkingQuotaDto {
    private Integer id;
    private Integer totalSlots;
}
