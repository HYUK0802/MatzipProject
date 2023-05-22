package com.jhkim.matzip.mappers;

import com.jhkim.matzip.entities.*;
import com.jhkim.matzip.enums.VerifyRecoverContactCodeResult;
import org.apache.catalina.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    int deleteRecoverEmailCode(RecoverEmailCodeEntity recoverEmailCode);
    int insertRecoverContactCode(RecoverContactCodeEntity recoverContactCode);
    int insertRecoverEmailCode(RecoverEmailCodeEntity recoverEmailCode);
    int insertRegisterContactCode(RegisterContactCodeEntity registerContactCode);
    int insertRegisterEmailCode(RegisterEmailCodeEntity registerEmailCode);
    int insertUser(UserEntity user);



    UserEntity selectUserByEmail(@Param(value = "email") String email);
    UserEntity selectUserByNickname(@Param(value = "nickname") String nickname);

    UserEntity selectUserByContact(@Param(value = "contact")String contact);

    RegisterContactCodeEntity selectRegisterContactCode(@Param(value = "contact")String contact,
                                                        @Param(value = "code")String code,
                                                        @Param(value = "salt")String salt);
    RegisterEmailCodeEntity VerifyRegisterEmailCode(@Param(value = "email")String email,
                                                    @Param(value = "code")String code,
                                                    @Param(value = "salt")String salt);
    RecoverContactCodeEntity selectRecoverContactCodeByContactCodeSalt(@Param(value = "contact")String contact,
                                                            @Param(value = "code")String code,
                                                            @Param(value = "salt")String salt);
    RecoverEmailCodeEntity selectRecoverEmailCodeByEmailCodeSalt(RecoverEmailCodeEntity recoverEmailCode);
    int updateUser(UserEntity user);
    int updateRegisterContactCode(RegisterContactCodeEntity registerCodeEntity);
    int updateRegisterEmailCode(RegisterEmailCodeEntity registerEmailCodeEntity);

    int updateRecoverContactCode(RecoverContactCodeEntity recoverContactCodeEntity);
    int updateRecoverEmailCode(RecoverEmailCodeEntity recoverEmailCode);

    RegisterContactCodeEntity selectRegisterContactCode(RegisterContactCodeEntity registerContactCode);
}

