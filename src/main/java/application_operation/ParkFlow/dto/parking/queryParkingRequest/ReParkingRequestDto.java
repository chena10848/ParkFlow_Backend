package application_operation.ParkFlow.dto.parking.queryParkingRequest;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class ReParkingRequestDto {
    private Integer totalSlotsId;
    private Integer totalSlots;
    private Integer remainingQuantity;
    private List<parkingRequest> parkingRequestList;

    @Data
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class parkingRequest {
        private Integer requestId;
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
