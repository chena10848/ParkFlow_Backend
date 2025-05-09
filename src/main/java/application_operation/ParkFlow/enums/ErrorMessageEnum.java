package application_operation.ParkFlow.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorMessageEnum {
    // 包含 Service、Dao、Utils 和 Exception 的錯誤訊息

    // UserService
    MAIL_ALREADY_REGISTER("業務邏輯錯誤：信箱不能重複註冊。"),
    NEED_REGISTER("身分驗證錯誤：請先註冊後再登入系統。"),

    // ValidUtils
    NOT_USER("身分驗證錯誤：使用者沒有權限使用此功能。"),
    NOT_FM("身分驗證錯誤：使用者沒有權限使用此功能。"),
    NOT_BEFORE_TODAY("業務邏輯錯誤：日期必須在今天以後。"),

    // UserDao
    NOT_FOUND_USER("資料庫內容錯誤：找不到對應的用戶。"),

    // ParkingService
    NEED_REQ_BEFORE_THIS_THURSDAY("業務邏輯錯誤：申請「下週」車位必須在本週四前申請；但如果申請「下下週及以後」的車位，則隨時可以申請。"),
    NOT_SET_PARKING_QUOTA("業務邏輯錯誤：本週尚未設定停車上限，無法申請。"),
    USER_ALREADY_RESERVED("業務邏輯錯誤：使用者已申請車位，無法重複申請。"),
    NO_PARKING("業務邏輯錯誤：車位已被申請完畢，無法再受理申請。"),
    NOT_FOUND_PARKING_REQ("資料庫內容錯誤：找不到對應的停車位申請紀錄。"),
    NOT_DUPLICATE_DATE("業務邏輯錯誤：日期資料不能重複。"),
    NOT_FOUND_PARKING_QUOTA("資料庫內容錯誤：找不到對應的停車位上限設定紀錄。"),
    PARKING_QUOTA_LESS_THAN_ZERO("業務邏輯錯誤：剩餘車位會小於 0。"),

    // JwtUtil
    TOKEN_ILLEGAL("身分驗證錯誤：不合法的 Token。"),
    TOKEN_NOT_FOUND_OR_ERROR("身分驗證錯誤：Token 不存在或格式錯誤。身分驗證錯誤：Token 不存在或格式錯誤。"),

    // GlobalExceptionHandler
    JSON_PARSE_ERROR("輸入格式錯誤：請檢查輸入的 Json 資料格式是否正確。"),
    DATE_PARSE_ERROR("輸入格式錯誤：請輸入有效的日期格式 (例如：2025-01-01T00:00:00)。"),
    REVIEW_STATUS_ERROR("輸入格式錯誤：審核狀態請輸入 REJECTED、APPROVED 或 REVIEW。");

    private final String message;
}
