package application_operation.ParkFlow.controller.users;

import application_operation.ParkFlow.Response.SuccessResponse;
import application_operation.ParkFlow.controller.users.payload.UserCreateRq;
import application_operation.ParkFlow.controller.users.payload.UserLoginRq;
import application_operation.ParkFlow.controller.users.payload.UserUpdateRq;
import application_operation.ParkFlow.dto.users.UserQueryDto;
import application_operation.ParkFlow.service.users.UsersService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/v1/users", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UsersController {

    private final UsersService usersService;

    @PostMapping("/create")
    @Operation(summary = "註冊使用者", description = "註冊使用者")
    public ResponseEntity<SuccessResponse<String>> create(@Valid @RequestBody UserCreateRq userCreateRq){
        String token = usersService.create(userCreateRq);   // Jwt Token

        return ResponseEntity.ok(SuccessResponse.<String>builder()
                .data(token)
                .build());
    }

    @PostMapping("/login")
    @Operation(summary = "驗證使用者", description = "驗證使用者")
    public ResponseEntity<SuccessResponse<String>> login(@Valid @RequestBody UserLoginRq userLoginRq){
        String response = usersService.login(userLoginRq);

        return ResponseEntity.ok(SuccessResponse.<String>builder()
                .data(response)  // 登入成功回傳 Token
                .build());
    }

    @PostMapping("/logout")
    @Operation(summary = "使用者登出", description = "使用者登出")
    public ResponseEntity<SuccessResponse<String>> logout(){
        usersService.logout();

        return ResponseEntity.ok(SuccessResponse.<String>builder()
                .data(null)  // 登出不回傳任何內容
                .build());
    }

    @PostMapping("/query")
    @Operation(summary = "查詢使用者資料", description = "查詢使用者資料")
    public ResponseEntity<SuccessResponse<UserQueryDto>> query(){
        UserQueryDto userData = usersService.query();

        return ResponseEntity.ok(SuccessResponse.<UserQueryDto>builder()
                .data(userData)
                .build());
    }

    @PutMapping("/update")
    @Operation(summary = "更新使用者資料", description = "更新使用者資料")
    public ResponseEntity<SuccessResponse<UserQueryDto>> update(@Valid @RequestBody UserUpdateRq userUpdateRq){
        UserQueryDto updateData = usersService.update(userUpdateRq);   // Jwt Token

        return ResponseEntity.ok(SuccessResponse.<UserQueryDto>builder()
                .data(updateData)
                .build());
    }
}
