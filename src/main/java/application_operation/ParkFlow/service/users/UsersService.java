package application_operation.ParkFlow.service.users;

import application_operation.ParkFlow.controller.users.payload.UserCreateRq;
import application_operation.ParkFlow.controller.users.payload.UserLoginRq;
import application_operation.ParkFlow.controller.users.payload.UserUpdateRq;
import application_operation.ParkFlow.dao.users.UserDao;
import application_operation.ParkFlow.dto.UsersBaseDto;
import application_operation.ParkFlow.dto.users.*;
import application_operation.ParkFlow.entity.UserEntity;
import application_operation.ParkFlow.enums.ErrorMessageEnum;
import application_operation.ParkFlow.enums.ResponseCodeEnum;
import application_operation.ParkFlow.exception.HandleException;
import application_operation.ParkFlow.jwtToken.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsersService {

    private final UserDao userDao;
    private final JwtUtil jwtUtil;

    // 新增(註冊)使用者
    public String create(UserCreateRq userCreateRq){

        UserCreateDto userCreateDto = new UserCreateDto();
        BeanUtils.copyProperties(userCreateRq, userCreateDto);

        // 信箱是否已經註冊過
        if(userDao.existEmail(userCreateDto.getEmail())){
            throw new HandleException(
                    ResponseCodeEnum.BUSINESS_ERROR.getResponseCode(),
                    ErrorMessageEnum.MAIL_ALREADY_REGISTER.getMessage());
        }

        return tokenTransfer(userDao.saveUser(userCreateDto));
    }

    // 登入
    public String login(UserLoginRq userLoginRq){

        UserLoginDto userLoginDto = new UserLoginDto();
        BeanUtils.copyProperties(userLoginRq, userLoginDto);

        // Email 不存在的話，回傳 0001
        if(!userDao.existEmail(userLoginDto.getEmail())){
            throw new HandleException(
                    ResponseCodeEnum.REGISTER_REQ.getResponseCode(),
                    ErrorMessageEnum.NEED_REGISTER.getMessage());
        }

        // 存在的話，用 Email 找出用戶資料並包裝成 Jwt
        return tokenTransfer(userDao.queryUserByEmail(userLoginDto));
    }

    // 登出
    public void logout(){
        // Jwt Token 驗證
        jwtUtil.validateToken();

        // 從 Authorization 標頭(authHeader) 取出 Token
        String token = jwtUtil.extractTokenFromAuthHeader();

        // 將 Token 加入黑名單
        jwtUtil.blacklistToken(token);
    }

    // 查詢使用者自己的用戶資料
    public UserQueryDto query(){
        // Jwt Token 驗證
        jwtUtil.validateToken();

        // 取得使用者個人資料
        UsersBaseDto usersBaseDto = jwtUtil.getUserBase();

        return userDao.queryUser(usersBaseDto.getUserId());
    }

    // 修改使用者自己的用戶資料
    public UserQueryDto update(UserUpdateRq userUpdateRq){

        // Jwt Token 驗證
        jwtUtil.validateToken();

        // 取得使用者個人資料
        UsersBaseDto usersBaseDto = jwtUtil.getUserBase();

        UserUpdateDto userUpdateDto = new UserUpdateDto();

        userUpdateDto.setId(usersBaseDto.getUserId());
        userUpdateDto.setChineseName(userUpdateRq.getChineseName());
        userUpdateDto.setEnglishName(userUpdateRq.getEnglishName());
        userUpdateDto.setEmail(userDao.findEmailById(usersBaseDto.getUserId()));
        userUpdateDto.setCellphone(userUpdateRq.getCellphone());
        userUpdateDto.setCarNumber(userUpdateRq.getCarNumber());
        userUpdateDto.setCarType(userUpdateRq.getCarType());

        UserEntity updateEntity =  userDao.updateUser(userUpdateDto);

        return userDao.queryUser(updateEntity.getId());
    }



    public String tokenTransfer(UserEntity userEntity){
        return jwtUtil.generateToken(
                userEntity.getId(),
                userDao.findRoleName(userEntity.getRoleId())
        );
    }
}
