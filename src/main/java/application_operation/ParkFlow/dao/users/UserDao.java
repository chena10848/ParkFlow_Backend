package application_operation.ParkFlow.dao.users;

import application_operation.ParkFlow.dto.UsersBaseDto;
import application_operation.ParkFlow.dto.mail.SendEmailDto;
import application_operation.ParkFlow.dto.parking.queryUserParkingRequest.QueryUserAndRoleDto;
import application_operation.ParkFlow.dto.users.UserCreateDto;
import application_operation.ParkFlow.dto.users.UserLoginDto;
import application_operation.ParkFlow.dto.users.UserQueryDto;
import application_operation.ParkFlow.dto.users.UserUpdateDto;
import application_operation.ParkFlow.entity.UserEntity;
import application_operation.ParkFlow.enums.ErrorMessageEnum;
import application_operation.ParkFlow.enums.ResponseCodeEnum;
import application_operation.ParkFlow.exception.HandleException;
import application_operation.ParkFlow.repository.RolesRepository;
import application_operation.ParkFlow.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserDao {

    private final UsersRepository usersRepository;
    private final RolesRepository rolesRepository;

    public Boolean existEmail(String email){
        return usersRepository.existsByEmail(email);
    }

    public String findRoleName(Integer roleId){
        return rolesRepository.findById(roleId).get().getRoleName();
    }

    public UserEntity queryUserByEmail(UserLoginDto userLoginDto){
        return usersRepository.findByEmail(userLoginDto.getEmail());
    }

    public String findEnglishNameById(Integer id){
        return usersRepository.findById(id).get().getEnglishName();
    }

    public String findEmailById(Integer id){
        return usersRepository.findById(id).get().getEmail();
    }


    public UserEntity saveUser(UserCreateDto userCreateDto){
        UserEntity userEntity = convertToCreateEntity(userCreateDto);
        return usersRepository.save(userEntity);
    }

    public UserQueryDto queryUser(Integer id){
        UserEntity userEntity =  usersRepository.findById(id)
                .orElseThrow(() -> new HandleException(
                        ResponseCodeEnum.DATABASE_ERROR.getResponseCode(),
                        ErrorMessageEnum.NOT_FOUND_USER.getMessage()));

        UserQueryDto userQueryDto = new UserQueryDto();

        userQueryDto.setId(userEntity.getId());
        userQueryDto.setChineseName(userEntity.getChineseName());
        userQueryDto.setEnglishName(userEntity.getEnglishName());
        userQueryDto.setEmail(userEntity.getEmail());
        userQueryDto.setCellphone(userEntity.getCellphone());
        userQueryDto.setCarNumber(userEntity.getCarNumber());
        userQueryDto.setCarType(userEntity.getCarType());

        return userQueryDto;
    }

    public UserEntity updateUser(UserUpdateDto userUpdateDto){
        UserEntity userEntity = convertToUpdateEntity(userUpdateDto);
        return usersRepository.save(userEntity);
    }



    @Transactional(readOnly = true)
    public QueryUserAndRoleDto findUsersAndRoleById(UsersBaseDto usersBaseDto) {

        List<Object[]> users = usersRepository.findUsersAndRoleById(usersBaseDto.getUserId());

        String roleName = (String) users.get(0)[0];
        String englishName = (String) users.get(0)[1];
        String email = (String) users.get(0)[2];

        return QueryUserAndRoleDto.builder().roleName(roleName).englishName(englishName).email(email).build();
    }

    @Transactional(readOnly = true)
    public SendEmailDto queryFMEmailData () {
        List<Object[]> list = usersRepository.findUsersAndRoleByFM();

        return SendEmailDto.builder()
                .name((String) list.get(0)[0])
                .email((String) list.get(0)[1])
                .build();
    }



    public UserEntity convertToCreateEntity(UserCreateDto userCreateDto){
        UserEntity userEntity = new UserEntity();

        userEntity.setChineseName(userCreateDto.getChineseName());
        userEntity.setEnglishName(userCreateDto.getEnglishName());
        userEntity.setEmail(userCreateDto.getEmail());
        userEntity.setCellphone(userCreateDto.getCellphone());
        userEntity.setCarNumber(userCreateDto.getCarNumber());
        userEntity.setCarType(userCreateDto.getCarType());

        return userEntity;
    }

    public UserEntity convertToUpdateEntity(UserUpdateDto userUpdateDto){
        UserEntity userEntity = new UserEntity();

        userEntity.setId(userUpdateDto.getId());
        userEntity.setChineseName(userUpdateDto.getChineseName());
        userEntity.setEnglishName(userUpdateDto.getEnglishName());
        userEntity.setEmail(userUpdateDto.getEmail());
        userEntity.setCellphone(userUpdateDto.getCellphone());
        userEntity.setCarNumber(userUpdateDto.getCarNumber());
        userEntity.setCarType(userUpdateDto.getCarType());

        return userEntity;
    }
}
