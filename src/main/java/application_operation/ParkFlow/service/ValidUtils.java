package application_operation.ParkFlow.service;

import application_operation.ParkFlow.dao.users.UserDao;
import application_operation.ParkFlow.enums.ErrorMessageEnum;
import application_operation.ParkFlow.enums.ResponseCodeEnum;
import application_operation.ParkFlow.exception.HandleException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ValidUtils {

    private final UserDao userDao;

    // 身分驗證 - User
    public void isUser(String roleName){
        if(!roleName.equals(userDao.findRoleName(1))) {
            throw new HandleException(
                    ResponseCodeEnum.AUTH_ERROR.getResponseCode(),
                    ErrorMessageEnum.NOT_USER.getMessage());
        }
    }

    // 身分驗證 - FM
    public void isFM(String roleName){
        if(!roleName.equals(userDao.findRoleName(2))) {
            throw new HandleException(
                    ResponseCodeEnum.AUTH_ERROR.getResponseCode(),
                    ErrorMessageEnum.NOT_FM.getMessage());
        }
    }

    // 日期是否在今天以後
    public void isBeforeDate(LocalDateTime startDate){
        if(startDate.isBefore(LocalDateTime.now())) {
            throw new HandleException(
                    ResponseCodeEnum.BUSINESS_ERROR.getResponseCode(),
                    ErrorMessageEnum.NOT_BEFORE_TODAY.getMessage());
        }
    }

    public Boolean isValidRequest(LocalDateTime now, LocalDateTime requestedDate, LocalDateTime startDate) {

        // 計算下下週開始時間
        LocalDateTime nextTwoWeekStartDate = startDate.plusWeeks(1);

        // 取得 "本週四 00:00"
        LocalDateTime thisThursday = now.toLocalDate()
                .with(DayOfWeek.THURSDAY)
                .atStartOfDay();

        // **條件 1：申請時間屬於「下週」範圍**
        Boolean isNextWeek = !requestedDate.isBefore(startDate) && requestedDate.isBefore(nextTwoWeekStartDate);

        // **條件 2：現在時間必須在「本週四之前」才能申請「下週」**
        Boolean isBeforeThursday = now.isBefore(thisThursday);

        // **如果申請的是「下週」，必須在「本週四前」申請**
        if (isNextWeek) {
            return isBeforeThursday;
        }

        // **如果申請的是「下下週及以後」，則隨時可以申請**
        return true;
    }
}