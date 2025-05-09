package application_operation.ParkFlow.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseCodeEnum {
    SUCCESS("0000"),
    REGISTER_REQ("0001"),

    INPUT_ERROR("9000"),
    AUTH_ERROR("9001"),
    BUSINESS_ERROR("9002"),
    DATABASE_ERROR("9003"),
    MAIL_ERROR("9004");

    private final String responseCode;
}
