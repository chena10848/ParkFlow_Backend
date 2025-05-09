package application_operation.ParkFlow.exceptionHandler;

import application_operation.ParkFlow.Response.ErrorResponse;
import application_operation.ParkFlow.enums.ErrorMessageEnum;
import application_operation.ParkFlow.enums.ParkingRequestEnum;
import application_operation.ParkFlow.enums.ResponseCodeEnum;
import application_operation.ParkFlow.exception.HandleException;
import application_operation.ParkFlow.exception.JwtTokenException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.format.DateTimeParseException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HandleException.class)
    public ResponseEntity<ErrorResponse> handleException(HandleException ex) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ErrorResponse
                        .builder()
                        .code(ex.getCode())
                        .message(ex.getMessage())
                        .data(null)
                        .build()
                );
    }

    @ExceptionHandler(JwtTokenException.class)
    public ResponseEntity<ErrorResponse> handleJwtTokenException(JwtTokenException ex) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ErrorResponse
                        .builder()
                        .code(ResponseCodeEnum.AUTH_ERROR.getResponseCode())
                        .message(ex.getMessage())
                        .data(null)
                        .build()
                );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> methodArgNotValidException(MethodArgumentNotValidException ex) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ErrorResponse
                        .builder()
                        .code(ResponseCodeEnum.INPUT_ERROR.getResponseCode())
                        .message(ex.getBindingResult().getAllErrors().get(0).getDefaultMessage())  // BindingResult 包含所有驗證失敗的錯誤訊息，AllErrors 是一個 List，包含所有錯誤訊息物件，這邊取第一個錯誤(通常只有一個)，DefaultMessage 是預設的錯誤訊息。
                        .data(null)
                        .build()
                );
    }

    // RequestBody(Json) 輸入格式有問題導致 Jackson 套件無法解析的拋錯
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> jsonParseException(HttpMessageNotReadableException ex) {

        Throwable rootCause = ex.getMostSpecificCause();

        String errorMessage = ErrorMessageEnum.JSON_PARSE_ERROR.getMessage();

        // 處理日期格式錯誤 (DateTimeParseException)
        if (rootCause instanceof DateTimeParseException) {
            errorMessage = ErrorMessageEnum.DATE_PARSE_ERROR.getMessage();
        }

        // 處理 Enum 轉換錯誤 (InvalidFormatException)
        if (rootCause instanceof InvalidFormatException invalidFormatException) {
            if (invalidFormatException.getTargetType().isEnum() && invalidFormatException.getTargetType().equals(ParkingRequestEnum.class)) {
                errorMessage = ErrorMessageEnum.REVIEW_STATUS_ERROR.getMessage();
            }
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(ErrorResponse
                        .builder()
                        .code(ResponseCodeEnum.INPUT_ERROR.getResponseCode())
                        .message(errorMessage)
                        .data(null)
                        .build()
                );
    }
}