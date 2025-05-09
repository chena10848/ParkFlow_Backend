package application_operation.ParkFlow.Response;

import application_operation.ParkFlow.enums.ResponseCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SuccessResponse<T> {
    @Builder.Default
    private String code = ResponseCodeEnum.SUCCESS.getResponseCode();

    @Builder.Default
    private String message = "Success";

    private T data;
}
