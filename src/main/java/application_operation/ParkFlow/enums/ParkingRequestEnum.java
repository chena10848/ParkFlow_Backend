package application_operation.ParkFlow.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ParkingRequestEnum {
    APPROVED("0"),
    REVIEW("1"),
    REJECTED("2");

    private final String code;

    public static String getNameByCode(String code) {
        for (ParkingRequestEnum status : ParkingRequestEnum.values()) {
            if (status.getCode().equals(code)) {
                return status.name();
            }
        }

        return code;
    }
}
