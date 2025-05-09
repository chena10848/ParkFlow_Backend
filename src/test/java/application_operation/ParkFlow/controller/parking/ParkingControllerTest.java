package application_operation.ParkFlow.controller.parking;

import application_operation.ParkFlow.controller.parking.payload.*;
import application_operation.ParkFlow.dto.parking.create.ParkingQuotaDto;
import application_operation.ParkFlow.dto.parking.create.ParkingRequestDto;
import application_operation.ParkFlow.dto.parking.queryParkingRequest.ReParkingRequestDto;
import application_operation.ParkFlow.dto.parking.queryUserParkingRequest.ReUserParkingRequestDto;
import application_operation.ParkFlow.dto.parking.update.UpdateParkingRequestDto;
import application_operation.ParkFlow.enums.ParkingRequestEnum;
import application_operation.ParkFlow.enums.ResponseCodeEnum;
import application_operation.ParkFlow.exceptionHandler.GlobalExceptionHandler;
import application_operation.ParkFlow.service.parking.ParkingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ParkingControllerTest {
    private MockMvc mockMvc;
    private ObjectMapper objectMapper; // 將物件轉換成 Json 字串

    @Mock
    private ParkingService parkingService;

    @InjectMocks
    private ParkingController parkingController;

    private static final String create = "/v1/parking/create-parking-request";
    private static final String update = "/v1/parking/update-parking-request";
    private static final String queryUserParkingRequest = "/v1/parking/query-user-parking-request";
    private static final String queryFmParkingRequest = "/v1/parking/query-fm-parking-request";
    private static final String createParkingQuotaPath = "/v1/parking/create-parking-quota";
    private static final String updateParkingQuotaPath = "/v1/parking/update-parking-quota";


    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());  // Jackson 不支援 LocalDateTime 類別轉換成 JSON，需另外注入支援 Java 8 時間類型的模組 (jackson-datatype-jsr310)

        mockMvc = MockMvcBuilders
                .standaloneSetup(parkingController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("ParkingController.createParkingQuota()_success")
    public void createParkingQuota_success() throws Exception{

        // 模擬回傳的 DTO
        ParkingQuotaDto parkingQuotaDto = new ParkingQuotaDto();
        parkingQuotaDto.setId(1);
        parkingQuotaDto.setTotalSlots(15);

        // 輸入的資料
        ParkingQuotaCreateRq parkingQuotaCreateRq = new ParkingQuotaCreateRq();
        parkingQuotaCreateRq.setStartDate(LocalDateTime.parse("2025-04-14T00:00:00"));
        parkingQuotaCreateRq.setTotalSlots(15);

        when(parkingService.createParkingQuota(any())).thenReturn(parkingQuotaDto);

        // 確認 Data 是否有內容，並回傳 0000 Success
        mockMvc.perform(post(createParkingQuotaPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(objectMapper.writeValueAsString(parkingQuotaCreateRq))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCodeEnum.SUCCESS.getResponseCode()))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.totalSlots").value(parkingQuotaCreateRq.getTotalSlots()));
    }

    @Test
    @DisplayName("ParkingController.createParkingQuota()_failed")
    public void createParkingQuota_failed() throws Exception{

        // 錯誤情境: 車位數量超過 Integer 範圍
        String totalSlotsExceedRq = """
                {
                    "startDate": "2025-04-14T00:00:00",
                    "totalSlots": 2147483648
                }
                """;

        // 車位數量超過 Integer 範圍，回傳 9000
        mockMvc.perform(post(createParkingQuotaPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(objectMapper.writeValueAsString(totalSlotsExceedRq))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCodeEnum.INPUT_ERROR.getResponseCode()))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("ParkingController.updateParkingQuota()_success")
    public void updateParkingQuota_success() throws Exception{

        // 模擬回傳的 DTO
        ParkingQuotaDto parkingQuotaDto = new ParkingQuotaDto();
        parkingQuotaDto.setId(1);
        parkingQuotaDto.setTotalSlots(30);

        // 輸入的資料
        ParkingQuotaUpdateRq parkingQuotaUpdateRq = new ParkingQuotaUpdateRq();
        parkingQuotaUpdateRq.setId(1);
        parkingQuotaUpdateRq.setTotalSlots(30);

        when(parkingService.updateParkingQuota(any())).thenReturn(parkingQuotaDto);

        // 確認 Data 是否有內容，並回傳 0000 Success
        mockMvc.perform(put(updateParkingQuotaPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(objectMapper.writeValueAsString(parkingQuotaUpdateRq))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCodeEnum.SUCCESS.getResponseCode()))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.id").value(parkingQuotaUpdateRq.getId()))
                .andExpect(jsonPath("$.data.totalSlots").value(parkingQuotaUpdateRq.getTotalSlots()));
    }

    @Test
    @DisplayName("ParkingController.updateParkingQuota()_failed")
    public void updateParkingQuota_failed() throws Exception{

        // 錯誤情境: id 沒有讀取到
        ParkingQuotaUpdateRq idNullRq = new ParkingQuotaUpdateRq();
        idNullRq.setId(null);
        idNullRq.setTotalSlots(30);

        // id 沒有讀取到，回傳 9000
        mockMvc.perform(post(createParkingQuotaPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(objectMapper.writeValueAsString(idNullRq))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCodeEnum.INPUT_ERROR.getResponseCode()))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("ParkingController.create()_success")
    public void create_success() throws Exception {

        String strToStartTime = "2025-04-06T00:00:00";
        String strToApplicationTime = "2025-04-02T14:39:43";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime startTime = LocalDateTime.parse(strToStartTime, formatter);
        LocalDateTime applicationTime = LocalDateTime.parse(strToApplicationTime, formatter);

        when(parkingService.create(any())).thenReturn(
                ParkingRequestDto.builder()
                        .Id(1)
                        .applicationTime(applicationTime)
                        .startDate(startTime)
                        .cellPhone("0912345678")
                        .carNumber("EAF-2200")
                        .carType("TOYOTA")
                        .build()
        );

        ParkingRequestCreateRq parkingRequestCreateRq = ParkingRequestCreateRq.builder()
                .startDate(startTime)
                .cellPhone("0912345678")
                .carNumber("EAF-2200")
                .carType("TOYOTA")
                .build();

        mockMvc.perform(post(create)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(objectMapper.writeValueAsString(parkingRequestCreateRq)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCodeEnum.SUCCESS.getResponseCode()))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.startDate").value(startTime.format(formatter)))
                .andExpect(jsonPath("$.data.cellPhone").value("0912345678"))
                .andExpect(jsonPath("$.data.carNumber").value("EAF-2200"))
                .andExpect(jsonPath("$.data.carType").value("TOYOTA"));
    }

    @Test
    @DisplayName("ParkingController.create()_failed")
    public void create_failed() throws Exception {

        String strToStartTime = "2025-04-06T00:00:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime startTime = LocalDateTime.parse(strToStartTime, formatter);

        ParkingRequestCreateRq parkingRequestCreateRq = ParkingRequestCreateRq.builder()
                .startDate(startTime)
                .cellPhone("091234567")
                .carNumber("EAF-2200")
                .carType("TOYOTA")
                .build();

        mockMvc.perform(post(create)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(objectMapper.writeValueAsString(parkingRequestCreateRq)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCodeEnum.INPUT_ERROR.getResponseCode()))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("ParkingController.update()_success")
    public void update_success() throws Exception {
        when(parkingService.update(any())).thenReturn(UpdateParkingRequestDto.builder()
                .id(1)
                .status(ParkingRequestEnum.APPROVED)
                .parkingSlotNumber(10)
                .build());

        ParkingRequestUpdateRq parkingRequestUpdateRq = ParkingRequestUpdateRq.builder()
                .id(1)
                .status(ParkingRequestEnum.APPROVED)
                .parkingSlotNumber(10)
                .build();

        mockMvc.perform(put(update)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(objectMapper.writeValueAsString(parkingRequestUpdateRq)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCodeEnum.SUCCESS.getResponseCode()))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.status").value(ParkingRequestEnum.APPROVED.toString()))
                .andExpect(jsonPath("$.data.parkingSlotNumber").value(10));
    }

    @Test
    @DisplayName("ParkingController.update()_failed")
    public void update_failed() throws Exception {

        ParkingRequestUpdateRq parkingRequestUpdateRq = ParkingRequestUpdateRq.builder()
                .status(ParkingRequestEnum.APPROVED)
                .parkingSlotNumber(10)
                .build();

        mockMvc.perform(put(update)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(objectMapper.writeValueAsString(parkingRequestUpdateRq)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCodeEnum.INPUT_ERROR.getResponseCode()))
                .andExpect(jsonPath("$.data").doesNotExist());
    }


    @Test
    @DisplayName("ParkingController.queryUserParkingRequest()_success")
    public void queryUserParkingRequest_success() throws Exception {

        String strToStartTime = "2025-04-06T00:00:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime startTime = LocalDateTime.parse(strToStartTime, formatter);

        when(parkingService.queryUserParkingRequest(any())).thenReturn(ReUserParkingRequestDto.builder()
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

        mockMvc.perform(post(queryUserParkingRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(objectMapper.writeValueAsString(queryUserParkingRequestRq)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCodeEnum.SUCCESS.getResponseCode()))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.parkingRequestList[0].requestTime").value(startTime.format(formatter)))
                .andExpect(jsonPath("$.data.parkingRequestList[0].name").value("小明"))
                .andExpect(jsonPath("$.data.parkingRequestList[0].carType").value("TOYOTA"))
                .andExpect(jsonPath("$.data.parkingRequestList[0].carNumber").value("EAF-2200"))
                .andExpect(jsonPath("$.data.parkingRequestList[0].cellphone").value("0912345678"))
                .andExpect(jsonPath("$.data.parkingRequestList[0].parkingSlotNumber").value(10))
                .andExpect(jsonPath("$.data.parkingRequestList[0].status").value(ParkingRequestEnum.APPROVED.toString()));
    }

    @Test
    @DisplayName("ParkingController.queryUserParkingRequest()_failed")
    public void queryUserParkingRequest_failed() throws Exception {
        QueryUserParkingRequestRq queryUserParkingRequestRq = QueryUserParkingRequestRq.builder()
                .build();

        mockMvc.perform(post(queryUserParkingRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(objectMapper.writeValueAsString(queryUserParkingRequestRq)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCodeEnum.INPUT_ERROR.getResponseCode()))
                .andExpect(jsonPath("$.message").value(ResourceBundle.getBundle("ValidationMessages", Locale.TAIWAN).getString("startDate.notnull")));
    }

    @Test
    @DisplayName("ParkingController.queryFmParkingRequest()_success")
    public void queryFmParkingRequest_success() throws Exception {

        String strToStartTime = "2025-04-06T00:00:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime startTime = LocalDateTime.parse(strToStartTime, formatter);

        when(parkingService.queryFmParkingRequest(any())).thenReturn(ReParkingRequestDto.builder()
                .parkingRequestList(List.of(ReParkingRequestDto.parkingRequest.builder()
                        .requestId(1)
                        .requestTime(startTime)
                        .name("小明")
                        .carType("TOYOTA")
                        .carNumber("EAF-2200")
                        .cellphone("0912345678")
                        .parkingSlotNumber(10)
                        .status(ParkingRequestEnum.APPROVED.toString())
                        .build()))
                .remainingQuantity(10)
                .totalSlotsId(1)
                .totalSlots(10)
                .build());

        QueryParkingRequestRq queryParkingRequestRq = QueryParkingRequestRq.builder()
                .startDate(startTime)
                .build();

        mockMvc.perform(post(queryFmParkingRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(objectMapper.writeValueAsString(queryParkingRequestRq)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCodeEnum.SUCCESS.getResponseCode()))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.parkingRequestList[0].requestId").value(1))
                .andExpect(jsonPath("$.data.parkingRequestList[0].requestTime").value(startTime.format(formatter)))
                .andExpect(jsonPath("$.data.parkingRequestList[0].name").value("小明"))
                .andExpect(jsonPath("$.data.parkingRequestList[0].carType").value("TOYOTA"))
                .andExpect(jsonPath("$.data.parkingRequestList[0].carNumber").value("EAF-2200"))
                .andExpect(jsonPath("$.data.parkingRequestList[0].cellphone").value("0912345678"))
                .andExpect(jsonPath("$.data.parkingRequestList[0].parkingSlotNumber").value(10))
                .andExpect(jsonPath("$.data.parkingRequestList[0].status").value(ParkingRequestEnum.APPROVED.toString()))
                .andExpect(jsonPath("$.data.remainingQuantity").value(10))
                .andExpect(jsonPath("$.data.totalSlotsId").value(1))
                .andExpect(jsonPath("$.data.totalSlots").value(10));
    }

    @Test
    @DisplayName("ParkingController.queryFmParkingRequest()_failed")
    public void queryFmParkingRequest_failed() throws Exception {
        QueryParkingRequestRq queryParkingRequestRq = QueryParkingRequestRq.builder()
                .build();

        mockMvc.perform(post(queryFmParkingRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(objectMapper.writeValueAsString(queryParkingRequestRq)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCodeEnum.INPUT_ERROR.getResponseCode()))
                .andExpect(jsonPath("$.message").value(ResourceBundle.getBundle("ValidationMessages", Locale.TAIWAN).getString("startDate.notnull")));
    }
}
