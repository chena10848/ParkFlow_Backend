package application_operation.ParkFlow.controller.parking;

import application_operation.ParkFlow.Response.SuccessResponse;
import application_operation.ParkFlow.controller.parking.payload.*;
import application_operation.ParkFlow.dto.parking.create.ParkingQuotaDto;
import application_operation.ParkFlow.dto.parking.create.ParkingRequestDto;
import application_operation.ParkFlow.dto.parking.queryParkingRequest.ReParkingRequestDto;
import application_operation.ParkFlow.dto.parking.queryUserParkingRequest.ReUserParkingRequestDto;
import application_operation.ParkFlow.dto.parking.update.UpdateParkingRequestDto;
import application_operation.ParkFlow.service.parking.ParkingService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "v1/parking/", produces = MediaType.APPLICATION_JSON_VALUE)
public class ParkingController {
    private final ParkingService parkingService;

    @Operation(summary = "申請停車位" ,description = "申請停車位")
    @PostMapping(value = "create-parking-request")
    public ResponseEntity<SuccessResponse<ParkingRequestDto>> create(@Valid @RequestBody ParkingRequestCreateRq parkingRequestCreateRq) {
        ParkingRequestDto parkingRequestDto = parkingService.create(parkingRequestCreateRq);

        return ResponseEntity.ok(SuccessResponse.<ParkingRequestDto>builder()
                .data(parkingRequestDto)
                .build());
    }

    @Operation(summary = "審核停車位" ,description = "審核停車位")
    @PutMapping(value = "update-parking-request")
    public ResponseEntity<SuccessResponse<UpdateParkingRequestDto>> update(@Valid @RequestBody ParkingRequestUpdateRq parkingRequestUpdateRq) {
        UpdateParkingRequestDto updateParkingRequestDto = parkingService.update(parkingRequestUpdateRq);

        return ResponseEntity.ok(SuccessResponse.<UpdateParkingRequestDto>builder()
                .data(updateParkingRequestDto)
                .build());
    }

    @Operation(summary = "取得一般使用者申請紀錄", description = "取得一般使用者申請紀錄")
    @PostMapping(value = "query-user-parking-request")
    public ResponseEntity<SuccessResponse<ReUserParkingRequestDto>> queryUserParkingRequest(@Valid @RequestBody QueryUserParkingRequestRq queryUserParkingRequestRq) {
        ReUserParkingRequestDto queryUserParkingRequestDto = parkingService.queryUserParkingRequest(queryUserParkingRequestRq);

        return ResponseEntity.ok(SuccessResponse.<ReUserParkingRequestDto>builder()
                .data(queryUserParkingRequestDto)
                .build());
    }

    @Operation(summary = "FM取得停車資訊", description = "FM取得停車資訊")
    @PostMapping(value = "query-fm-parking-request")
    public ResponseEntity<SuccessResponse<ReParkingRequestDto>> queryFmParkingRequest(@Valid @RequestBody QueryParkingRequestRq queryParkingRequestRq) {
        ReParkingRequestDto queryParkingRequest = parkingService.queryFmParkingRequest(queryParkingRequestRq);

        return ResponseEntity.ok(SuccessResponse.<ReParkingRequestDto>builder()
                .data(queryParkingRequest)
                .build());
    }

    @Operation(summary = "新增下週停車數量" ,description = "新增下週停車數量")
    @PostMapping(value = "create-parking-quota")
    public ResponseEntity<SuccessResponse<ParkingQuotaDto>> createParkingQuota(@Valid @RequestBody ParkingQuotaCreateRq parkingQuotaCreateRq) {
        ParkingQuotaDto parkingQuota = parkingService.createParkingQuota(parkingQuotaCreateRq);

        return ResponseEntity.ok(SuccessResponse.<ParkingQuotaDto>builder()
                .data(parkingQuota)
                .build());
    }

    @Operation(summary = "更新下週停車數量" ,description = "更新下週停車數量")
    @PutMapping(value = "update-parking-quota")
    public ResponseEntity<SuccessResponse<ParkingQuotaDto>> updateParkingQuota(@Valid @RequestBody ParkingQuotaUpdateRq parkingQuotaUpdateRq) {
        ParkingQuotaDto updateParkingQuota = parkingService.updateParkingQuota(parkingQuotaUpdateRq);

        return ResponseEntity.ok(SuccessResponse.<ParkingQuotaDto>builder()
                .data(updateParkingQuota)
                .build());
    }
}
