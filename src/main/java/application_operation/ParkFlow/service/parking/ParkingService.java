package application_operation.ParkFlow.service.parking;

import application_operation.ParkFlow.config.EmailConfig;
import application_operation.ParkFlow.controller.parking.payload.*;
import application_operation.ParkFlow.dao.users.UserDao;
import application_operation.ParkFlow.dto.UsersBaseDto;
import application_operation.ParkFlow.dto.mail.EmailDto;
import application_operation.ParkFlow.dto.mail.SendEmailDto;
import application_operation.ParkFlow.dto.parking.create.ParkingQuotaCreateDto;
import application_operation.ParkFlow.dto.parking.create.ParkingQuotaDto;
import application_operation.ParkFlow.dto.parking.create.ParkingRequestCreateDto;
import application_operation.ParkFlow.dto.parking.create.ParkingRequestDto;
import application_operation.ParkFlow.dto.parking.queryParkingRequest.QueryParkingRequestDto;
import application_operation.ParkFlow.dto.parking.queryUserParkingRequest.QueryUserAndRoleDto;
import application_operation.ParkFlow.dto.parking.queryUserParkingRequest.QueryUserParkingRequestDto;
import application_operation.ParkFlow.dto.parking.queryParkingRequest.ReParkingRequestDto;
import application_operation.ParkFlow.dto.parking.queryUserParkingRequest.ReUserParkingRequestDto;
import application_operation.ParkFlow.dto.parking.update.ParkingQuotaUpdateDto;
import application_operation.ParkFlow.dto.parking.update.ParkingRequestUpdateDto;
import application_operation.ParkFlow.dto.parking.update.UpdateParkingRequestDto;
import application_operation.ParkFlow.entity.ParkingQuotaEntity;
import application_operation.ParkFlow.entity.ParkingRequestEntity;
import application_operation.ParkFlow.enums.ErrorMessageEnum;
import application_operation.ParkFlow.enums.ParkingRequestEnum;
import application_operation.ParkFlow.enums.ResponseCodeEnum;
import application_operation.ParkFlow.exception.HandleException;
import application_operation.ParkFlow.jwtToken.JwtUtil;
import application_operation.ParkFlow.service.ValidUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import application_operation.ParkFlow.dao.parking.ParkingDao;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ParkingService {

    @Value("${send.mail.to.application}")
    private boolean isSendApplicationMail;

    @Value("${send.email.to.request}")
    private boolean isSendRequestMail;

    private final ParkingDao parkingDao;
    private final UserDao userDao;
    private final JwtUtil jwtUtil;
    private final ValidUtils validUtils;
    private final EmailConfig emailConfig;

    public ParkingRequestDto create (ParkingRequestCreateRq parkingRequestCreateRq) {

        // Jwt Token 驗證
        jwtUtil.validateToken();

        LocalDateTime requestedDate = parkingRequestCreateRq.getStartDate();
        LocalDateTime now = LocalDateTime.now(); // 取得當前時間
        LocalDateTime startDate = LocalDateTime.now()
                .with(DayOfWeek.SUNDAY) // 設定為這周日
                .toLocalDate()
                .atStartOfDay(); // 計算下週開始時間

        if(!validUtils.isValidRequest(now, requestedDate, startDate)) {
            throw new HandleException(
                    ResponseCodeEnum.BUSINESS_ERROR.getResponseCode(),
                    ErrorMessageEnum.NEED_REQ_BEFORE_THIS_THURSDAY.getMessage());
        }

        // 取得使用者個人資料
        UsersBaseDto usersBaseDto = jwtUtil.getUserBase();

        // 驗證是不是一般使用者
        validUtils.isUser(usersBaseDto.getRoleName());

        // Rq -> Dto
        ParkingRequestCreateDto parkingRequestCreateDto = ParkingRequestCreateDto.builder()
                .startDate(parkingRequestCreateRq.getStartDate())
                .cellPhone(parkingRequestCreateRq.getCellPhone())
                .carNumber(parkingRequestCreateRq.getCarNumber())
                .carType(parkingRequestCreateRq.getCarType())
                .build();

        // 日期是否在今天以後
        validUtils.isBeforeDate(parkingRequestCreateDto.getStartDate());

        // 檢查申請的日期是否已設定停車位上限
        if(!parkingDao.existsByStartDate(parkingRequestCreateDto.getStartDate())){
            throw new HandleException(
                    ResponseCodeEnum.BUSINESS_ERROR.getResponseCode(),
                    ErrorMessageEnum.NOT_SET_PARKING_QUOTA.getMessage());
        }

        //檢查相同使用者是否重複申請
        if(!parkingDao.findParkingRequestByApplicantId(parkingRequestCreateDto, usersBaseDto)) {
            throw new HandleException(
                    ResponseCodeEnum.BUSINESS_ERROR.getResponseCode(),
                    ErrorMessageEnum.USER_ALREADY_RESERVED.getMessage());
        }

        //檢查申請是否到達上限
        if(!parkingDao.findParkingRequestCheckQuota(parkingRequestCreateDto)) {
            throw new HandleException(
                    ResponseCodeEnum.BUSINESS_ERROR.getResponseCode(),
                    ErrorMessageEnum.NO_PARKING.getMessage());
        }

        //寫入申請表
        ParkingRequestDto parkingRequestDto = parkingDao.saveParkingRequest(parkingRequestCreateDto, usersBaseDto);

        //send email
        sendRequestMail(parkingRequestDto, usersBaseDto);

        return parkingRequestDto;
    }

    public void sendRequestMail(ParkingRequestDto parkingRequestDto, UsersBaseDto usersBaseDto) {
        if(isSendRequestMail) {
            SendEmailDto sendEmailDto = userDao.queryFMEmailData();

            EmailDto emailDto = new EmailDto();
            emailDto.setEmail(sendEmailDto.getEmail());
            emailDto.setSubject(String.format("[申請] 停車位使用申請 - %s", sendEmailDto.getName()));
            emailDto.setText(String.format("""
                    Dear FM
                
                    申請資訊
                    申請日期 : %s
                    車牌號碼 : %s
                    車輛類型 : %s
                    申請人員 : %s
                    聯絡電話 : %s
                
                    此信件為系統自動發送，如有任何問題，請聯繫申請人。
                    """, parkingRequestDto.getApplicationTime(),
                    parkingRequestDto.getCarNumber(),
                    parkingRequestDto.getCarType(),
                    userDao.findEnglishNameById(usersBaseDto.getUserId()),
                    parkingRequestDto.getCellPhone()));


            emailConfig.consumeEmail(emailDto);
        }
    }

    public UpdateParkingRequestDto update(ParkingRequestUpdateRq parkingRequestUpdateRq) {

        // Jwt Token 驗證
        jwtUtil.validateToken();

        // 取得使用者個人資料
        UsersBaseDto usersBaseDto = jwtUtil.getUserBase();

        // 檢查權限為 FM
        validUtils.isFM(usersBaseDto.getRoleName());

        // Rq -> Dto
        ParkingRequestUpdateDto parkingRequestUpdateDto = ParkingRequestUpdateDto
                .builder()
                .id(parkingRequestUpdateRq.getId())
                .status(parkingRequestUpdateRq.getStatus())
                .parkingSlotNumber(parkingRequestUpdateRq.getParkingSlotNumber())
                .build();

        // 資料庫是否有對應的內容
        if(!parkingDao.existsByParkingRequestId(parkingRequestUpdateDto.getId())){
            throw new HandleException(
                    ResponseCodeEnum.DATABASE_ERROR.getResponseCode(),
                    ErrorMessageEnum.NOT_FOUND_PARKING_REQ.getMessage());
        }

        // 取得申請資料
        List<ParkingRequestEntity> parkingRequestEntities = parkingDao.findParkingRequestById(parkingRequestUpdateDto);

        // 更新資料
        UpdateParkingRequestDto updateParkingRequestDto = parkingDao.updateParkingRequest(parkingRequestEntities.get(0), parkingRequestUpdateDto, usersBaseDto);

        // send email
        sendApplicationMail(parkingRequestEntities, parkingRequestUpdateRq);

        return updateParkingRequestDto;
    }

    public void sendApplicationMail(List<ParkingRequestEntity> parkingRequestEntities, ParkingRequestUpdateRq parkingRequestUpdateRq) {
        if(isSendApplicationMail) {
            UsersBaseDto usersBaseDto1 = new UsersBaseDto();
            usersBaseDto1.setUserId(parkingRequestEntities.get(0).getApplicantId());
            QueryUserAndRoleDto queryUserAndRoleDto = userDao.findUsersAndRoleById(usersBaseDto1);

            EmailDto emailDto = new EmailDto();
            emailDto.setEmail(queryUserAndRoleDto.getEmail());
            emailDto.setSubject(String.format("[申請] 停車位申請結果 - %s", queryUserAndRoleDto.getEnglishName()));
            emailDto.setText(String.format("""
                    Dear %s
                
                    申請資訊
                    申請日期 : %s
                    申請人員 : %s
                    車型 : %s
                    車牌號碼 : %s
                    聯絡電話 : %s
                    車位號碼 : %s
                
                    此信件為系統自動發送，如有任何問題，請聯繫 FM 諮詢。
                    """,
                    queryUserAndRoleDto.getEnglishName(),
                    parkingRequestEntities.get(0).getApplicationTime(),
                    parkingRequestEntities.get(0).getCarNumber(),
                    parkingRequestEntities.get(0).getCarType(),
                    queryUserAndRoleDto.getEnglishName(),
                    parkingRequestEntities.get(0).getCellPhone(),
                    parkingRequestUpdateRq.getParkingSlotNumber()));


            emailConfig.consumeEmail(emailDto);
        }
    }

    public ReUserParkingRequestDto queryUserParkingRequest(QueryUserParkingRequestRq queryUserParkingRequestRq) {

        // Jwt Token 驗證
        jwtUtil.validateToken();

        // 取得使用者個人資料
        UsersBaseDto usersBaseDto = jwtUtil.getUserBase();

        // 驗證是不是一般使用者
        validUtils.isUser(userDao.findUsersAndRoleById(usersBaseDto).getRoleName());

        // Rq -> Dto
        QueryUserParkingRequestDto queryUserParkingRequestDto = QueryUserParkingRequestDto.builder()
                .startDate(queryUserParkingRequestRq.getStartDate())
                .build();

        // 搜尋結果
        return parkingDao.findUserParkingRequest(queryUserParkingRequestDto, usersBaseDto);
    }

    public ReParkingRequestDto queryFmParkingRequest(QueryParkingRequestRq queryParkingRequestRq) {

        // Jwt Token 驗證
        jwtUtil.validateToken();

        // 取得使用者個人資料
        UsersBaseDto usersBaseDto = jwtUtil.getUserBase();

        // 驗證是不是FM
        validUtils.isFM(userDao.findUsersAndRoleById(usersBaseDto).getRoleName());

        // Rq -> Dto
        QueryParkingRequestDto queryParkingRequestDto = QueryParkingRequestDto.builder()
                .startDate(queryParkingRequestRq.getStartDate())
                .build();

        List<ReParkingRequestDto.parkingRequest> parkingRequest = parkingDao.findParkingRequest(queryParkingRequestDto);
        ParkingQuotaEntity entity = parkingDao.findParkingQuotaByStartDate(queryParkingRequestDto.getStartDate());
        int parkingRequestCount = (int) parkingRequest.stream()
                .filter(x -> x.getStatus().equals(ParkingRequestEnum.APPROVED.name()) || x.getStatus().equals(ParkingRequestEnum.REVIEW.name()))
                .count();
        Integer totalSlots = ObjectUtils.isEmpty(entity) ? Integer.valueOf(0) : entity.getTotalSlots();
        Integer remainingQuantity = totalSlots - parkingRequestCount;
        Integer totalSlotId = ObjectUtils.isEmpty(entity) ? null : entity.getId();

        // 搜尋結果
        ReParkingRequestDto reParkingRequestDto = new ReParkingRequestDto();
        reParkingRequestDto.setParkingRequestList(parkingRequest);
        reParkingRequestDto.setTotalSlots(totalSlots);
        reParkingRequestDto.setRemainingQuantity(remainingQuantity);
        reParkingRequestDto.setTotalSlotsId(totalSlotId);

        return reParkingRequestDto;
    }

    public ParkingQuotaDto createParkingQuota(ParkingQuotaCreateRq parkingQuotaCreateRq){

        // Jwt Token 驗證
        jwtUtil.validateToken();

        // 取得使用者個人資料
        UsersBaseDto usersBaseDto = jwtUtil.getUserBase();

        // 檢查權限為 FM
        validUtils.isFM(usersBaseDto.getRoleName());

        ParkingQuotaCreateDto parkingQuotaCreateDto = new ParkingQuotaCreateDto();
        BeanUtils.copyProperties(parkingQuotaCreateRq, parkingQuotaCreateDto);

        // 日期是否在今天以後
        validUtils.isBeforeDate(parkingQuotaCreateDto.getStartDate());

        // 日期是否有重複
        if(parkingDao.existsByStartDate(parkingQuotaCreateDto.getStartDate())){
            throw new HandleException(
                    ResponseCodeEnum.BUSINESS_ERROR.getResponseCode(),
                    ErrorMessageEnum.NOT_DUPLICATE_DATE.getMessage());
        }

        ParkingQuotaEntity saveEntity = parkingDao.saveParkingQuota(parkingQuotaCreateDto);
        return ParkingQuotaDto.builder()
                .id(saveEntity.getId())
                .totalSlots(saveEntity.getTotalSlots())
                .build();
    }

    public ParkingQuotaDto updateParkingQuota(ParkingQuotaUpdateRq parkingQuotaUpdateRq){

        // Jwt Token 驗證
        jwtUtil.validateToken();

        // 取得使用者個人資料
        UsersBaseDto usersBaseDto = jwtUtil.getUserBase();

        // 檢查權限為 FM
        validUtils.isFM(usersBaseDto.getRoleName());

        ParkingQuotaUpdateDto parkingQuotaUpdateDto = new ParkingQuotaUpdateDto();
        BeanUtils.copyProperties(parkingQuotaUpdateRq, parkingQuotaUpdateDto);

        // 資料庫是否有對應的內容
        if(!parkingDao.existsByParkingQuotaId(parkingQuotaUpdateDto.getId())){
            throw new HandleException(
                    ResponseCodeEnum.DATABASE_ERROR.getResponseCode(),
                    ErrorMessageEnum.NOT_FOUND_PARKING_QUOTA.getMessage());
        }

        // 更新後剩餘停車位是否會小於 0
        ParkingQuotaEntity parkingQuotaRecord = parkingDao.findParkingQuotaById(parkingQuotaUpdateDto.getId());
        if(parkingQuotaUpdateDto.getTotalSlots() - parkingDao.getAllReservedSlots(parkingQuotaRecord.getStartDate()) < 0){
            throw new HandleException(
                    ResponseCodeEnum.BUSINESS_ERROR.getResponseCode(),
                    ErrorMessageEnum.PARKING_QUOTA_LESS_THAN_ZERO.getMessage());
        }

        ParkingQuotaEntity updateEntity = parkingDao.updateParkingQuota(parkingQuotaUpdateDto);
        return ParkingQuotaDto.builder()
                .id(updateEntity.getId())
                .totalSlots(updateEntity.getTotalSlots())
                .build();
    }
}
