package application_operation.ParkFlow.dto.parking.queryUserParkingRequest;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ReUserParkingRequestDto {
    private List<parkingRequest> parkingRequestList;
    private Integer remainingQuantity;

    @Data
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class parkingRequest {
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime requestTime;
        private String name;
        private String carType;
        private String carNumber;
        private String cellphone;
        private Integer parkingSlotNumber;
        private String status;
    }
}
