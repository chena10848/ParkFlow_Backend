package application_operation.ParkFlow.dto.parking.queryUserParkingRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class QueryUserAndRoleDto {

    private String roleName;
    private String englishName;
    private String email;
}
