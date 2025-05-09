package application_operation.ParkFlow.service.parking;

import application_operation.ParkFlow.controller.parking.payload.ParkingQuotaCreateRq;
import application_operation.ParkFlow.controller.parking.payload.ParkingQuotaUpdateRq;
import application_operation.ParkFlow.controller.parking.payload.ParkingRequestCreateRq;
import application_operation.ParkFlow.controller.parking.payload.ParkingRequestUpdateRq;
import application_operation.ParkFlow.controller.parking.payload.QueryParkingRequestRq;
import application_operation.ParkFlow.controller.parking.payload.QueryUserParkingRequestRq;
import application_operation.ParkFlow.dao.parking.ParkingDao;
import application_operation.ParkFlow.dao.users.UserDao;
import application_operation.ParkFlow.dto.UsersBaseDto;
import application_operation.ParkFlow.dto.parking.create.ParkingQuotaDto;
import application_operation.ParkFlow.dto.parking.create.ParkingRequestDto;
import application_operation.ParkFlow.dto.parking.queryParkingRequest.ReParkingRequestDto;
import application_operation.ParkFlow.dto.parking.queryUserParkingRequest.QueryUserAndRoleDto;
import application_operation.ParkFlow.dto.parking.queryUserParkingRequest.ReUserParkingRequestDto;
import application_operation.ParkFlow.dto.parking.update.UpdateParkingRequestDto;
import application_operation.ParkFlow.entity.ParkingQuotaEntity;
import application_operation.ParkFlow.entity.ParkingRequestEntity;
import application_operation.ParkFlow.enums.ErrorMessageEnum;
import application_operation.ParkFlow.enums.ParkingRequestEnum;
import application_operation.ParkFlow.enums.ResponseCodeEnum;
import application_operation.ParkFlow.exception.HandleException;
import application_operation.ParkFlow.jwtToken.JwtUtil;
import application_operation.ParkFlow.service.ValidUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

    @InjectMocks
    private ParkingService parkingService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private ValidUtils validUtils;

    @Mock
    private UserDao userDao;

    @Mock
    private ParkingDao parkingDao;

    @Test
    @DisplayName("ParkingService.create()_success")
    public void create_success() {
        // Jwt Token 驗證
        doNothing().when(jwtUtil).validateToken();
        //申請日期檢查
        when(validUtils.isValidRequest(any(), any(), any())).thenReturn(true);
        //取得使用者資訊
        when(jwtUtil.getUserBase()).thenReturn(UsersBaseDto.builder()
                .userId(1)
                .roleName("User")
                .build());
        //驗證使用者權限
        doNothing().when(validUtils).isUser(any());
        // 日期是否在今天以後
        doNothing().when(validUtils).isBeforeDate(any());
        // 日期是否有重複
        when(parkingDao.existsByStartDate(any())).thenReturn(true);
        //檢查相同使用者是否重複申請
        when(parkingDao.findParkingRequestByApplicantId(any(), any())).thenReturn(true);
        //檢查申請是否到達上限
        when(parkingDao.findParkingRequestCheckQuota(any())).thenReturn(true);

        String strToStartTime = "2025-04-06T00:00:00";
        String strToApplicationTime = "2025-04-02T14:39:43";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime startTime = LocalDateTime.parse(strToStartTime, formatter);
        LocalDateTime applicationTime = LocalDateTime.parse(strToApplicationTime, formatter);
        //執行寫入
        when(parkingDao.saveParkingRequest(any(), any())).thenReturn(ParkingRequestDto.builder()
                .Id(1)
                .applicationTime(applicationTime)
                .startDate(startTime)
                .cellPhone("0912345678")
                .carNumber("EAF-2200")
                .carType("TOYOTA")
                .build());

        ParkingRequestCreateRq parkingRequestCreateRq = ParkingRequestCreateRq.builder()
                .startDate(startTime)
                .cellPhone("0912345678")
                .carNumber("EAF-2200")
                .carType("TOYOTA")
                .build();

        ParkingRequestDto parkingRequestDto = parkingService.create(parkingRequestCreateRq);

        assertNotNull(parkingRequestDto);
        assertEquals(startTime.format(formatter), parkingRequestDto.getStartDate().format(formatter));
        assertEquals(applicationTime.format(formatter), parkingRequestDto.getApplicationTime().format(formatter));
        assertEquals("0912345678", parkingRequestDto.getCellPhone());
        assertEquals("EAF-2200", parkingRequestDto.getCarNumber());
        assertEquals("TOYOTA", parkingRequestDto.getCarType());
    }

    @Test
    @DisplayName("ParkingService.create()_failed")
    public void create_failed() {
        // Jwt Token 驗證
        doNothing().when(jwtUtil).validateToken();
        //申請日期檢查
        when(validUtils.isValidRequest(any(), any(), any())).thenReturn(true);
        //取得使用者資訊
        when(jwtUtil.getUserBase()).thenReturn(UsersBaseDto.builder()
                .userId(1)
                .roleName("User")
                .build());
        //驗證使用者權限
        doNothing().when(validUtils).isUser(any());
        // 日期是否在今天以後
        doNothing().when(validUtils).isBeforeDate(any());
        // 日期是否有重複
        when(parkingDao.existsByStartDate(any())).thenReturn(false);

        String strToStartTime = "2025-04-06T00:00:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime startTime = LocalDateTime.parse(strToStartTime, formatter);

        ParkingRequestCreateRq parkingRequestCreateRq =ParkingRequestCreateRq.builder()
                .startDate(startTime)
                .cellPhone("0912345678")
                .carNumber("EAF-2200")
                .carType("TOYOTA")
                .build();

        // 驗證是否有拋出 Exception
        HandleException exception = assertThrows(HandleException.class, () ->
                // 執行Service測試
                parkingService.create(parkingRequestCreateRq)
        );

        // 驗證錯誤碼
        assertEquals(ResponseCodeEnum.BUSINESS_ERROR.getResponseCode(), exception.getCode());
        assertEquals(ErrorMessageEnum.NOT_SET_PARKING_QUOTA.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("ParkingService.update()_success")
    public void update_success() {
        // Jwt Token 驗證
        doNothing().when(jwtUtil).validateToken();
        //取得使用者資訊
        when(jwtUtil.getUserBase()).thenReturn(UsersBaseDto.builder()
                .userId(1)
                .roleName("User")
                .build());
        //驗證使用者權限
        doNothing().when(validUtils).isFM(any());
        // 資料庫是否有對應的內容
        when(parkingDao.existsByParkingRequestId(any())).thenReturn(true);

        List<ParkingRequestEntity> mockList = new ArrayList<>();
        ParkingRequestEntity entity = new ParkingRequestEntity();
        entity.setId(1);
        entity.setCellPhone("0912345678");
        entity.setCarNumber("EAF-2200");
        entity.setCarType("TOYOTA");
        entity.setStatus(ParkingRequestEnum.APPROVED);
        mockList.add(entity);

        when(parkingDao.findParkingRequestById(any())).thenReturn(mockList);

        when(parkingDao.updateParkingRequest(any(), any(), any())).thenReturn(UpdateParkingRequestDto.builder()
                .id(1)
                .status(ParkingRequestEnum.APPROVED)
                .parkingSlotNumber(10)
                .build()
        );

        ParkingRequestUpdateRq parkingRequestUpdateRq = ParkingRequestUpdateRq.builder()
                .id(1)
                .status(ParkingRequestEnum.APPROVED)
                .parkingSlotNumber(10)
                .build();

        UpdateParkingRequestDto updateParkingRequestDto = parkingService.update(parkingRequestUpdateRq);

        assertNotNull(updateParkingRequestDto);
        assertEquals(1, updateParkingRequestDto.getId());
        assertEquals(ParkingRequestEnum.APPROVED, updateParkingRequestDto.getStatus());
        assertEquals(10, updateParkingRequestDto.getParkingSlotNumber());
    }

    @Test
    @DisplayName("ParkingService.update()_failed")
    public void update_failed() {
        // Jwt Token 驗證
        doNothing().when(jwtUtil).validateToken();
        //取得使用者資訊
        when(jwtUtil.getUserBase()).thenReturn(UsersBaseDto.builder()
                .userId(1)
                .roleName("User")
                .build());
        //驗證使用者權限
        doNothing().when(validUtils).isFM(any());
        // 資料庫是否有對應的內容
        when(parkingDao.existsByParkingRequestId(any())).thenReturn(false);

        ParkingRequestUpdateRq parkingRequestUpdateRq = ParkingRequestUpdateRq.builder()
                .id(1)
                .status(ParkingRequestEnum.APPROVED)
                .parkingSlotNumber(10)
                .build();

        // 驗證是否有拋出 Exception
        HandleException exception = assertThrows(HandleException.class, () ->
                // 執行Service測試
                parkingService.update(parkingRequestUpdateRq)
        );

        // 驗證錯誤碼
        assertEquals(ResponseCodeEnum.DATABASE_ERROR.getResponseCode(), exception.getCode());
        assertEquals(ErrorMessageEnum.NOT_FOUND_PARKING_REQ.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("ParkingService.queryUserParkingRequest()_success")
    public void queryUserParkingRequest_success() {
        // Jwt Token 驗證
        doNothing().when(jwtUtil).validateToken();
        //取得使用者資訊
        when(jwtUtil.getUserBase()).thenReturn(UsersBaseDto.builder()
                .userId(1)
                .roleName("USER")
                .build());

        //取得使用者資訊
        when(userDao.findUsersAndRoleById(any())).thenReturn(QueryUserAndRoleDto.builder()
                .email("min@gmail.com")
                .englishName("min")
                .roleName("USER").build());

        //驗證使用者權限
        doNothing().when(validUtils).isUser(any());

        String strToStartTime = "2025-04-06T00:00:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime startTime = LocalDateTime.parse(strToStartTime, formatter);

        when(parkingDao.findUserParkingRequest(any(), any())).thenReturn(ReUserParkingRequestDto.builder()
                .parkingRequestList(List.of(ReUserParkingRequestDto.parkingRequest.builder()
                        .requestTime(startTime)
                        .name("小明")
                        .carType("TOYOTA")
                        .carNumber("EAF-2200")
                        .cellphone("0912345678")
                        .parkingSlotNumber(10)
                        .status(ParkingRequestEnum.APPROVED.toString())
                        .build()))
                .remainingQuantity(10)
                .build());

        QueryUserParkingRequestRq queryUserParkingRequestRq = QueryUserParkingRequestRq.builder()
                .startDate(startTime)
                .build();

        ReUserParkingRequestDto reUserParkingRequestDto = parkingService.queryUserParkingRequest(queryUserParkingRequestRq);

        assertNotNull(reUserParkingRequestDto);
        assertEquals(startTime, reUserParkingRequestDto.getParkingRequestList().get(0).getRequestTime());
        assertEquals("小明", reUserParkingRequestDto.getParkingRequestList().get(0).getName());
        assertEquals("TOYOTA", reUserParkingRequestDto.getParkingRequestList().get(0).getCarType());
        assertEquals("EAF-2200", reUserParkingRequestDto.getParkingRequestList().get(0).getCarNumber());
        assertEquals("0912345678", reUserParkingRequestDto.getParkingRequestList().get(0).getCellphone());
        assertEquals(10, reUserParkingRequestDto.getParkingRequestList().get(0).getParkingSlotNumber());
        assertEquals(ParkingRequestEnum.APPROVED.toString(), reUserParkingRequestDto.getParkingRequestList().get(0).getStatus());
        assertEquals(10, reUserParkingRequestDto.getRemainingQuantity());
    }

    @Test
    @DisplayName("ParkingService.queryUserParkingRequest()_failed")
    public void queryUserParkingRequest_failed() {
        doNothing().when(jwtUtil).validateToken();

        when(jwtUtil.getUserBase()).thenReturn(
                UsersBaseDto.builder()
                        .userId(1)
                        .roleName("FM")
                        .build()
        );

        when(userDao.findUsersAndRoleById(any())).thenReturn(QueryUserAndRoleDto.builder()
                .email("min@gmail.com")
                .englishName("min")
                .roleName("FM").build());

        doThrow(new HandleException(ResponseCodeEnum.AUTH_ERROR.getResponseCode(),
                ErrorMessageEnum.NOT_USER.getMessage())).when(validUtils).isUser(any());

        String strToStartTime = "2025-04-06T00:00:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime startTime = LocalDateTime.parse(strToStartTime, formatter);

        QueryUserParkingRequestRq queryUserParkingRequestRq = QueryUserParkingRequestRq.builder()
                .startDate(startTime)
                .build();

        HandleException exception = assertThrows(HandleException.class, () ->
                parkingService.queryUserParkingRequest(queryUserParkingRequestRq)
        );

        assertEquals(ResponseCodeEnum.AUTH_ERROR.getResponseCode(), exception.getCode());
        assertEquals(ErrorMessageEnum.NOT_USER.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("ParkingService.createParkingQuota()_success")
    public void createParkingQuota_success() {
        // 建立 Rq
        ParkingQuotaCreateRq parkingQuotaCreateRq = new ParkingQuotaCreateRq();
        parkingQuotaCreateRq.setStartDate(LocalDateTime.parse("2025-01-01T00:00:00"));
        parkingQuotaCreateRq.setTotalSlots(30);

        // 建立用戶驗證資訊
        UsersBaseDto usersBaseDto = new UsersBaseDto();
        usersBaseDto.setUserId(1);
        usersBaseDto.setRoleName("FM");

        // 建立 DAO 回傳的資料
        ParkingQuotaEntity parkingQuotaEntity = new ParkingQuotaEntity();
        parkingQuotaEntity.setId(1);
        parkingQuotaEntity.setStartDate(LocalDateTime.parse("2025-01-01T00:00:00"));
        parkingQuotaEntity.setTotalSlots(30);

        // 模擬 JWT 驗證通過的情境
        doNothing().when(jwtUtil).validateToken();

        // 模擬取得用戶驗證資訊的情境
        when(jwtUtil.getUserBase()).thenReturn(usersBaseDto);

        // 模擬權限為 FM 的情境
        doNothing().when(validUtils).isFM(any());

        // 模擬日期在今天之後的情境
        doNothing().when(validUtils).isBeforeDate(any());

        // 模擬日期沒有重複的情境
        when(parkingDao.existsByStartDate(any())).thenReturn(false);

        // 模擬 .saveParkingQuota() 執行後回傳 Entity 的情境
        when(parkingDao.saveParkingQuota(any())).thenReturn(parkingQuotaEntity);

        // 執行 .saveParkingQuota()
        ParkingQuotaDto result = parkingService.createParkingQuota(parkingQuotaCreateRq);

        // 驗證所有方法都有執行
        verify(jwtUtil).validateToken();
        verify(jwtUtil).getUserBase();
        verify(validUtils).isFM(any());
        verify(validUtils).isBeforeDate(any());
        verify(parkingDao).existsByStartDate(any());
        verify(parkingDao).saveParkingQuota(any());

        // 驗證回傳的 DTO 符合預期
        assertNotNull(result);
        assertEquals(result.getTotalSlots(), parkingQuotaCreateRq.getTotalSlots());
    }


    @Test
    @DisplayName("ParkingService.createParkingQuota()_failed")
    public void createParkingQuota_failed() {
        // 錯誤情境：輸入的日期在今天之前
        // 建立 Rq
        ParkingQuotaCreateRq parkingQuotaCreateRq = new ParkingQuotaCreateRq();
        parkingQuotaCreateRq.setStartDate(LocalDateTime.parse("2023-01-01T00:00:00"));
        parkingQuotaCreateRq.setTotalSlots(30);

        // 建立用戶驗證資訊
        UsersBaseDto usersBaseDto = new UsersBaseDto();
        usersBaseDto.setUserId(1);
        usersBaseDto.setRoleName("FM");

        // 模擬 JWT 驗證通過的情境
        doNothing().when(jwtUtil).validateToken();

        // 模擬取得用戶驗證資訊的情境
        when(jwtUtil.getUserBase()).thenReturn(usersBaseDto);

        // 模擬權限為 FM 的情境
        doNothing().when(validUtils).isFM(any());

        // 模擬日期在今天之前的情境
        doThrow(new HandleException(
                ResponseCodeEnum.BUSINESS_ERROR.getResponseCode(),
                ErrorMessageEnum.NOT_BEFORE_TODAY.getMessage())
        ).when(validUtils).isBeforeDate(any());

        // 執行 parkingService.createParkingQuota() 驗證拋出的錯誤是否為 HandleException，並保存成變數 ex
        HandleException ex = assertThrows(
                HandleException.class,
                () -> parkingService.createParkingQuota(parkingQuotaCreateRq)
        );

        // 驗證該執行的方法都有執行
        verify(jwtUtil).validateToken();
        verify(jwtUtil).getUserBase();
        verify(validUtils).isFM(any());
        verify(validUtils).isBeforeDate(any());
        verify(parkingDao, never()).existsByStartDate(any());
        verify(parkingDao, never()).saveParkingQuota(any());

        assertEquals(ResponseCodeEnum.BUSINESS_ERROR.getResponseCode(), ex.getCode());
        assertEquals(ErrorMessageEnum.NOT_BEFORE_TODAY.getMessage(), ex.getMessage());
    }

    @Test
    @DisplayName("ParkingService.updateParkingQuota()_success")
    public void updateParkingQuota_success() {
        // 建立 Rq
        ParkingQuotaUpdateRq parkingQuotaUpdateRq = new ParkingQuotaUpdateRq();
        parkingQuotaUpdateRq.setId(1);
        parkingQuotaUpdateRq.setTotalSlots(20);

        // 建立用戶驗證資訊
        UsersBaseDto usersBaseDto = new UsersBaseDto();
        usersBaseDto.setUserId(1);
        usersBaseDto.setRoleName("FM");

        // 建立查詢車位數量資訊時回傳的 Entity
        ParkingQuotaEntity parkingQuotaRecord = new ParkingQuotaEntity();
        parkingQuotaRecord.setId(1);
        parkingQuotaRecord.setStartDate(LocalDateTime.parse("2025-01-01T00:00:00"));
        parkingQuotaRecord.setTotalSlots(30);

        // 建立 DAO 回傳的資料
        ParkingQuotaEntity parkingQuotaEntity = new ParkingQuotaEntity();
        parkingQuotaEntity.setId(1);
        parkingQuotaEntity.setStartDate(LocalDateTime.parse("2025-01-01T00:00:00"));
        parkingQuotaEntity.setTotalSlots(20);

        // 模擬 JWT 驗證通過的情境
        doNothing().when(jwtUtil).validateToken();

        // 模擬取得用戶驗證資訊的情境
        when(jwtUtil.getUserBase()).thenReturn(usersBaseDto);

        // 模擬權限為 FM 的情境
        doNothing().when(validUtils).isFM(any());

        // 模擬資料庫有對應資料的情境
        when(parkingDao.existsByParkingQuotaId(any())).thenReturn(true);

        // 模擬更新後剩餘車位不會小於 0 的情境，假設當週已有 5 個車位被預約
        when(parkingDao.findParkingQuotaById(any())).thenReturn(parkingQuotaRecord);
        when(parkingDao.getAllReservedSlots(any())).thenReturn(5);

        // 模擬 .updateParkingQuota() 執行後回傳 Entity 的情境
        when(parkingDao.updateParkingQuota(any())).thenReturn(parkingQuotaEntity);

        // 執行 .updateParkingQuota()
        ParkingQuotaDto result = parkingService.updateParkingQuota(parkingQuotaUpdateRq);

        // 驗證所有方法都有執行
        verify(jwtUtil).validateToken();
        verify(jwtUtil).getUserBase();
        verify(validUtils).isFM(any());
        verify(parkingDao).existsByParkingQuotaId(any());
        verify(parkingDao).findParkingQuotaById(any());
        verify(parkingDao).getAllReservedSlots(any());
        verify(parkingDao).updateParkingQuota(any());

        // 驗證回傳的 DTO 符合預期
        assertNotNull(result);
        assertEquals(result.getId(), parkingQuotaUpdateRq.getId());
        assertEquals(result.getTotalSlots(), parkingQuotaUpdateRq.getTotalSlots());
    }


    @Test
    @DisplayName("ParkingService.updateParkingQuota()_failed")
    public void updateParkingQuota_failed() {
        // 錯誤情境：更新後剩餘車位會小於 0
        // 建立 Rq
        ParkingQuotaUpdateRq parkingQuotaUpdateRq = new ParkingQuotaUpdateRq();
        parkingQuotaUpdateRq.setId(1);
        parkingQuotaUpdateRq.setTotalSlots(3);

        // 建立用戶驗證資訊
        UsersBaseDto usersBaseDto = new UsersBaseDto();
        usersBaseDto.setUserId(1);
        usersBaseDto.setRoleName("FM");

        // 建立查詢車位數量資訊時回傳的 Entity
        ParkingQuotaEntity parkingQuotaRecord = new ParkingQuotaEntity();
        parkingQuotaRecord.setId(1);
        parkingQuotaRecord.setStartDate(LocalDateTime.parse("2025-01-01T00:00:00"));
        parkingQuotaRecord.setTotalSlots(30);

        // 模擬 JWT 驗證通過的情境
        doNothing().when(jwtUtil).validateToken();

        // 模擬取得用戶驗證資訊的情境
        when(jwtUtil.getUserBase()).thenReturn(usersBaseDto);

        // 模擬權限為 FM 的情境
        doNothing().when(validUtils).isFM(any());

        // 模擬資料庫有對應資料的情境
        when(parkingDao.existsByParkingQuotaId(any())).thenReturn(true);

        // 模擬更新後剩餘車位小於 0 的情境，假設當週已有 5 個車位被預約
        when(parkingDao.findParkingQuotaById(any())).thenReturn(parkingQuotaRecord);
        when(parkingDao.getAllReservedSlots(any())).thenReturn(5);

        // 執行 parkingService.updateParkingQuota() 驗證拋出的錯誤是否為 HandleException，並保存成變數 ex
        HandleException ex = assertThrows(
                HandleException.class,
                () -> parkingService.updateParkingQuota(parkingQuotaUpdateRq)
        );

        // 驗證該執行的方法都有執行
        verify(jwtUtil).validateToken();
        verify(jwtUtil).getUserBase();
        verify(validUtils).isFM(any());
        verify(parkingDao).existsByParkingQuotaId(any());
        verify(parkingDao).findParkingQuotaById(any());
        verify(parkingDao).getAllReservedSlots(any());
        verify(parkingDao, never()).updateParkingQuota(any());

        assertEquals(ResponseCodeEnum.BUSINESS_ERROR.getResponseCode(), ex.getCode());
        assertEquals(ErrorMessageEnum.PARKING_QUOTA_LESS_THAN_ZERO.getMessage(), ex.getMessage());
    }

    @Test
    @DisplayName("ParkingService.queryFmParkingRequest()_success")
    public void queryFmParkingRequest_success() {

        // Jwt Token 驗證
        doNothing().when(jwtUtil).validateToken();
        //取得使用者資訊
        when(jwtUtil.getUserBase()).thenReturn(UsersBaseDto.builder()
                .userId(1)
                .roleName("FM")
                .build());

        //取得使用者資訊
        when(userDao.findUsersAndRoleById(any())).thenReturn(QueryUserAndRoleDto.builder()
                .email("min@gmail.com")
                .englishName("min")
                .roleName("FM").build());

        //驗證使用者權限
        doNothing().when(validUtils).isFM(any());

        String strToStartTime = "2025-04-06T00:00:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime startTime = LocalDateTime.parse(strToStartTime, formatter);

        when(parkingDao.findParkingRequest(any())).thenReturn(List.of(
                ReParkingRequestDto.parkingRequest.builder()
                        .requestId(1)
                        .requestTime(startTime)
                        .name("小明")
                        .carType("TOYOTA")
                        .carNumber("EAF-2200")
                        .cellphone("0912345678")
                        .parkingSlotNumber(10)
                        .status(ParkingRequestEnum.APPROVED.toString())
                        .build()
        ));

        ParkingQuotaEntity parkingQuotaEntity = new ParkingQuotaEntity();
        parkingQuotaEntity.setStartDate(startTime);
        parkingQuotaEntity.setId(1);
        parkingQuotaEntity.setTotalSlots(5);

        when(parkingDao.findParkingQuotaByStartDate(any())).thenReturn(parkingQuotaEntity);

        QueryParkingRequestRq queryParkingRequestRq = QueryParkingRequestRq.builder()
                .startDate(startTime)
                .build();

        ReParkingRequestDto reUserParkingRequestDto = parkingService.queryFmParkingRequest(queryParkingRequestRq);

        assertNotNull(reUserParkingRequestDto);
        assertEquals(1, reUserParkingRequestDto.getParkingRequestList().get(0).getRequestId());
        assertEquals(startTime, reUserParkingRequestDto.getParkingRequestList().get(0).getRequestTime());
        assertEquals("小明", reUserParkingRequestDto.getParkingRequestList().get(0).getName());
        assertEquals("TOYOTA", reUserParkingRequestDto.getParkingRequestList().get(0).getCarType());
        assertEquals("EAF-2200", reUserParkingRequestDto.getParkingRequestList().get(0).getCarNumber());
        assertEquals("0912345678", reUserParkingRequestDto.getParkingRequestList().get(0).getCellphone());
        assertEquals(10, reUserParkingRequestDto.getParkingRequestList().get(0).getParkingSlotNumber());
        assertEquals(ParkingRequestEnum.APPROVED.toString(), reUserParkingRequestDto.getParkingRequestList().get(0).getStatus());
        assertEquals(5, reUserParkingRequestDto.getTotalSlots());
        assertEquals(1, reUserParkingRequestDto.getTotalSlotsId());
    }

    @Test
    @DisplayName("ParkingService.queryFmParkingRequest()_failed")
    public void queryFmParkingRequest_failed() {
        doNothing().when(jwtUtil).validateToken();

        when(jwtUtil.getUserBase()).thenReturn(
                UsersBaseDto.builder()
                        .userId(1)
                        .roleName("USER")
                        .build()
        );

        when(userDao.findUsersAndRoleById(any())).thenReturn(QueryUserAndRoleDto.builder()
                .email("min@gmail.com")
                .englishName("min")
                .roleName("USER").build());

        doThrow(new HandleException(ResponseCodeEnum.AUTH_ERROR.getResponseCode(),
                ErrorMessageEnum.NOT_FM.getMessage())).when(validUtils).isFM(any());

        String strToStartTime = "2025-04-06T00:00:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime startTime = LocalDateTime.parse(strToStartTime, formatter);

        QueryParkingRequestRq queryParkingRequestRq = QueryParkingRequestRq.builder()
                .startDate(startTime)
                .build();
      
        HandleException exception = assertThrows(HandleException.class, () ->
                parkingService.queryFmParkingRequest(queryParkingRequestRq)
        );

        assertEquals(ResponseCodeEnum.AUTH_ERROR.getResponseCode(), exception.getCode());
        assertEquals(ErrorMessageEnum.NOT_FM.getMessage(), exception.getMessage());
    }
}
