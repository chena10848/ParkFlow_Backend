package application_operation.ParkFlow.service.users;

import application_operation.ParkFlow.controller.users.payload.UserCreateRq;
import application_operation.ParkFlow.controller.users.payload.UserLoginRq;
import application_operation.ParkFlow.controller.users.payload.UserUpdateRq;
import application_operation.ParkFlow.dao.users.UserDao;
import application_operation.ParkFlow.dto.UsersBaseDto;
import application_operation.ParkFlow.dto.users.UserQueryDto;
import application_operation.ParkFlow.entity.UserEntity;
import application_operation.ParkFlow.enums.ErrorMessageEnum;
import application_operation.ParkFlow.enums.ResponseCodeEnum;
import application_operation.ParkFlow.exception.HandleException;
import application_operation.ParkFlow.exception.JwtTokenException;
import application_operation.ParkFlow.jwtToken.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsersServiceTest {

    @Mock
    private UserDao userDao;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UsersService usersService;

    @Test
    @DisplayName("UsersService.create()_success")
    public void create_success() {

        // 1. 準備階段 (Arrange)
        // 建立 Rq
        UserCreateRq userCreateRq = new UserCreateRq();
        userCreateRq.setChineseName("小明");
        userCreateRq.setEnglishName("Xiao Ming");
        userCreateRq.setEmail("xm@gmail.com");
        userCreateRq.setCellphone("0912345678");
        userCreateRq.setCarNumber("ABC-1234");
        userCreateRq.setCarType("TOYOTA");

        // 建立 DAO 層回傳的資料，因為這邊不能真的進入 DB。
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1);
        userEntity.setChineseName("小明");
        userEntity.setEnglishName("Xiao Ming");
        userEntity.setEmail("xm@gmail.com");
        userEntity.setCellphone("0912345678");
        userEntity.setCarNumber("ABC-1234");
        userEntity.setCarType("TOYOTA");
        userEntity.setRoleId(1);

        // 模擬信箱不重複的情境
        when(userDao.existEmail(any())).thenReturn(false);

        // 模擬 UserDao.saveUser() 執行後回傳結果的情境。
        when(userDao.saveUser(any())).thenReturn(userEntity);

        // 模擬生成 Jwt 的情境
        when(jwtUtil.generateToken(any(), any())).thenReturn("mock-jwt-string");


        // 2. 執行階段 (Act)
        // 執行 UsersService.create()。
        String token = usersService.create(userCreateRq);

        // 3. 驗證階段 (Assert)
        // 驗證 create() 的三個方法都有被執行
        verify(userDao).existEmail(any());
        verify(userDao).saveUser(any());
        verify(jwtUtil).generateToken(any(), any());

        // 驗證 token 不是 Null
        assertNotNull(token);
    }

    @Test
    @DisplayName("UsersService.create()_failed")
    public void create_failed() {
        // 錯誤情境：信箱已被註冊過
        // 建立 Rq
        UserCreateRq userCreateRq = new UserCreateRq();
        userCreateRq.setChineseName("小明");
        userCreateRq.setEnglishName("Xiao Ming");
        userCreateRq.setEmail("xiaoming@gmail.com");
        userCreateRq.setCellphone("0912345678");
        userCreateRq.setCarNumber("ABC-1234");
        userCreateRq.setCarType("TOYOTA");

        // 模擬信箱重複的情境
        when(userDao.existEmail(any())).thenReturn(true);

        // 執行 usersService.create 驗證拋出的錯誤是否為 HandleException，並保存成變數 ex
        HandleException ex = assertThrows(
                HandleException.class,
                () -> usersService.create(userCreateRq)
        );

        // 驗證只有信箱檢驗方法被執行，新增資料和建立 Token 未執行
        verify(userDao).existEmail(any());
        verify(userDao, never()).saveUser(any());
        verify(jwtUtil, never()).generateToken(any(), any());

        // 驗證 ErrorMessage 的 code 是 9002
        assertEquals(ResponseCodeEnum.BUSINESS_ERROR.getResponseCode(), ex.getCode());

        // 驗證錯誤訊息的內容
        assertEquals(ErrorMessageEnum.MAIL_ALREADY_REGISTER.getMessage(), ex.getMessage());
    }

    @Test
    @DisplayName("UsersService.login()_success")
    public void login_success() {

        // 建立 Rq
        UserLoginRq userLoginRq = new UserLoginRq();
        userLoginRq.setEmail("xm@gmail.com");

        // 建立 DAO 層回傳的資料，因為這邊不能真的進入 DB。
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1);
        userEntity.setChineseName("小明");
        userEntity.setEnglishName("Xiao Ming");
        userEntity.setEmail("xm@gmail.com");
        userEntity.setCellphone("0912345678");
        userEntity.setCarNumber("ABC-1234");
        userEntity.setCarType("TOYOTA");
        userEntity.setRoleId(1);

        // 模擬信箱已完成註冊的情境
        when(userDao.existEmail(any())).thenReturn(true);

        // 模擬 UserDao.queryUserByEmail() 執行後回傳結果的情境。
        when(userDao.queryUserByEmail(any())).thenReturn(userEntity);

        // 模擬生成 Jwt 的情境
        when(jwtUtil.generateToken(any(), any())).thenReturn("mock-jwt-string");

        // 執行 UsersService.login()。
        String token = usersService.login(userLoginRq);

        // 驗證 login() 的三個方法都執行過
        verify(userDao).existEmail(any());
        verify(userDao).queryUserByEmail(any());
        verify(jwtUtil).generateToken(any(), any());

        // 驗證 token 不是 Null
        assertNotNull(token);
    }

    @Test
    @DisplayName("UsersService.login()_failed")
    public void login_failed() {
        // 錯誤情境：信箱格式正確，但沒有註冊過
        // 建立 Rq
        UserLoginRq userLoginRq = new UserLoginRq();
        userLoginRq.setEmail("xiaoming112233@gmail.com");

        // 模擬信箱未完成註冊的情境
        when(userDao.existEmail(any())).thenReturn(false);

        // 執行 usersService.login 驗證拋出的錯誤是否為 HandleException，並保存成變數 ex
        HandleException ex = assertThrows(
                HandleException.class,
                () -> usersService.login(userLoginRq)
        );

        // 驗證只有信箱檢驗方法被執行，查詢資料和建立 Token 未執行
        verify(userDao).existEmail(any());
        verify(userDao, never()).queryUserByEmail(any());
        verify(jwtUtil, never()).generateToken(any(), any());

        // 驗證 ErrorMessage 的 code 是 0001
        assertEquals(ResponseCodeEnum.REGISTER_REQ.getResponseCode(), ex.getCode());

        // 驗證錯誤訊息的內容
        assertEquals(ErrorMessageEnum.NEED_REGISTER.getMessage(), ex.getMessage());
    }

    @Test
    @DisplayName("UsersService.logout()_success")
    public void logout_success() {

        // 模擬 JWT 驗證成功的情境，這裡不會實際執行驗證動作
        doNothing().when(jwtUtil).validateToken();

        String token = "mock-jwt-string";
        // 模擬從 Authorization 取出 Token
        when(jwtUtil.extractTokenFromAuthHeader()).thenReturn(token);

        // 模擬將 Jwt 存入黑名單的情境，這裡不會實際執行
        doNothing().when(jwtUtil).blacklistToken(token);

        // 執行 UsersService.logout()。
        usersService.logout();

        // 驗證 logout 的三個方法都有被執行過
        verify(jwtUtil).validateToken();
        verify(jwtUtil).extractTokenFromAuthHeader();
        verify(jwtUtil).blacklistToken(token);
    }


    @Test
    @DisplayName("UsersService.logout()_failed")
    public void logout_failed() {
        // 錯誤情境：Token 遺失或過期
        // 模擬 JWT 驗證未通過的情境
        String errorMessage = ErrorMessageEnum.TOKEN_ILLEGAL.getMessage();
        doThrow(new JwtTokenException(errorMessage)).when(jwtUtil).validateToken();

        // 執行 usersService.logout 驗證拋出的錯誤是否為 JwtTokenException，並保存成變數 ex
        JwtTokenException ex = assertThrows(
                JwtTokenException.class,
                () -> usersService.logout()
        );

        // 驗證只有 validateToken() 有被執行過
        verify(jwtUtil).validateToken();
        verify(jwtUtil, never()).extractTokenFromAuthHeader();
        verify(jwtUtil, never()).blacklistToken(any());

        // 驗證錯誤訊息的內容
        assertEquals(errorMessage, ex.getMessage());
    }


    @Test
    @DisplayName("UsersService.query()_success")
    public void query_success() {

        // 建立存放 JWT 資訊的 DTO
        UsersBaseDto usersBaseDto = new UsersBaseDto();
        usersBaseDto.setUserId(1);
        usersBaseDto.setRoleName("USER");

        // 建立查詢後回傳的用戶資料
        UserQueryDto userQueryDto = new UserQueryDto();
        userQueryDto.setId(1);
        userQueryDto.setChineseName("小明");
        userQueryDto.setEnglishName("Xiao Ming");
        userQueryDto.setEmail("xm@gmail.com");
        userQueryDto.setCellphone("0912345678");
        userQueryDto.setCarNumber("ABC-1234");
        userQueryDto.setCarType("TOYOTA");

        // 模擬 JWT 驗證成功的情境，這裡不會實際執行驗證動作
        doNothing().when(jwtUtil).validateToken();

        // 模擬從 JWT 取出用戶資訊並轉成 DTO 的情境
        when(jwtUtil.getUserBase()).thenReturn(usersBaseDto);

        // 模擬執行 queryUser() 回傳用戶資料的情境
        when(userDao.queryUser(any())).thenReturn(userQueryDto);

        // 執行 userService.query()
        UserQueryDto result = usersService.query();

        // 驗證三個方法是否都有執行
        verify(jwtUtil).validateToken();
        verify(jwtUtil).getUserBase();
        verify(userDao).queryUser(any());

        // 驗證回傳的 DTO 是否符合預期
        assertNotNull(result);
        assertEquals(result.getId(), userQueryDto.getId());
        assertEquals(result.getChineseName(), userQueryDto.getChineseName());
        assertEquals(result.getEnglishName(), userQueryDto.getEnglishName());
        assertEquals(result.getEmail(), userQueryDto.getEmail());
        assertEquals(result.getCellphone(), userQueryDto.getCellphone());
        assertEquals(result.getCarNumber(), userQueryDto.getCarNumber());
        assertEquals(result.getCarType(), userQueryDto.getCarType());
    }


    @Test
    @DisplayName("UsersService.query()_failed")
    public void query_failed() {

        // 錯誤情境：標頭的 JWT 不存在或格式錯誤
        // 模擬從標頭取出 JWT 的過程發生錯誤
        String errorMessage = ErrorMessageEnum.TOKEN_NOT_FOUND_OR_ERROR.getMessage();

        doNothing().when(jwtUtil).validateToken();
        doThrow(new JwtTokenException(errorMessage)).when(jwtUtil).getUserBase();

        // 執行 usersService.query 驗證拋出的錯誤是否為 JwtTokenException，並保存成變數 ex
        JwtTokenException ex = assertThrows(
                JwtTokenException.class,
                () -> usersService.query()
        );

        // 驗證只有 queryUser() 沒被執行過
        verify(jwtUtil).validateToken();
        verify(jwtUtil).getUserBase();
        verify(userDao, never()).queryUser(any());

        // 驗證錯誤訊息的內容
        assertEquals(errorMessage, ex.getMessage());
    }


    @Test
    @DisplayName("UsersService.update()_success")
    public void update_success() {

        // 建立存放 JWT 資訊的 DTO
        UsersBaseDto usersBaseDto = new UsersBaseDto();
        usersBaseDto.setUserId(1);
        usersBaseDto.setRoleName("USER");

        // 建立 Rq
        UserUpdateRq userUpdateRq = new UserUpdateRq();
        userUpdateRq.setChineseName("小明");
        userUpdateRq.setEnglishName("Xiao Ming");
        userUpdateRq.setCellphone("0912345678");
        userUpdateRq.setCarNumber("ABC-1234");
        userUpdateRq.setCarType("TOYOTA");

        // 建立更新後回傳的用戶資料
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1);
        userEntity.setChineseName("小明");
        userEntity.setEnglishName("Xiao Ming");
        userEntity.setEmail("xm@gmail.com");
        userEntity.setCellphone("0912345678");
        userEntity.setCarNumber("ABC-1234");
        userEntity.setCarType("TOYOTA");
        userEntity.setRoleId(1);

        // 建立更新後重新查詢的用戶資料
        UserQueryDto userQueryDto = new UserQueryDto();
        userQueryDto.setId(1);
        userQueryDto.setChineseName("小明");
        userQueryDto.setEnglishName("Xiao Ming");
        userQueryDto.setEmail("xm@gmail.com");
        userQueryDto.setCellphone("0912345678");
        userQueryDto.setCarNumber("ABC-1234");
        userQueryDto.setCarType("TOYOTA");

        // 模擬 JWT 驗證成功的情境，這裡不會實際執行驗證動作
        doNothing().when(jwtUtil).validateToken();

        // 模擬從 JWT 取出用戶資訊並轉成 DTO 的情境
        when(jwtUtil.getUserBase()).thenReturn(usersBaseDto);

        // 模擬執行 updateUser() 回傳用戶資料的情境
        when(userDao.updateUser(any())).thenReturn(userEntity);

        // 模擬執行 queryUser() 回傳用戶資料的情境
        when(userDao.queryUser(any())).thenReturn(userQueryDto);

        // 執行 userService.query()
        UserQueryDto result = usersService.update(userUpdateRq);

        // 驗證四個方法是否都有執行
        verify(jwtUtil).validateToken();
        verify(jwtUtil).getUserBase();
        verify(userDao).updateUser(any());
        verify(userDao).queryUser(any());

        // 驗證回傳的 DTO 是否符合預期
        assertNotNull(result);
        assertEquals(result.getId(), userQueryDto.getId());
        assertEquals(result.getChineseName(), userQueryDto.getChineseName());
        assertEquals(result.getEnglishName(), userQueryDto.getEnglishName());
        assertEquals(result.getEmail(), userQueryDto.getEmail());
        assertEquals(result.getCellphone(), userQueryDto.getCellphone());
        assertEquals(result.getCarNumber(), userQueryDto.getCarNumber());
        assertEquals(result.getCarType(), userQueryDto.getCarType());
    }


    @Test
    @DisplayName("UsersService.update()_failed")
    public void update_failed() {

        // 錯誤情境：Token 身分驗證錯誤

        // 建立 Rq
        UserUpdateRq UserUpdateRq = new UserUpdateRq();
        UserUpdateRq.setChineseName("小明");
        UserUpdateRq.setEnglishName("Xiao Ming");
        UserUpdateRq.setCellphone("0912345678");
        UserUpdateRq.setCarNumber("ABC-1234");
        UserUpdateRq.setCarType("TOYOTA");

        // 模擬 JWT 驗證未通過的情境
        String errorMessage = ErrorMessageEnum.TOKEN_ILLEGAL.getMessage();
        doThrow(new JwtTokenException(errorMessage)).when(jwtUtil).validateToken();

        // 執行 usersService.update 驗證拋出的錯誤是否為 JwtTokenException，並保存成變數 ex
        JwtTokenException ex = assertThrows(
                JwtTokenException.class,
                () -> usersService.update(new UserUpdateRq())
        );

        // 驗證是否只有 validateToken 執行
        verify(jwtUtil).validateToken();
        verify(jwtUtil, never()).getUserBase();
        verify(userDao, never()).updateUser(any());
        verify(userDao, never()).queryUser(any());

        // 驗證回傳的錯誤訊息是否正確
        assertEquals(errorMessage, ex.getMessage());
    }
}