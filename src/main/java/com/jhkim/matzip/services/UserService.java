package com.jhkim.matzip.services;

import com.jhkim.matzip.entities.*;
import com.jhkim.matzip.enums.*;
import com.jhkim.matzip.mappers.UserMapper;
import com.jhkim.matzip.utils.CryptoUtil;
import com.jhkim.matzip.utils.NCloudUtil;
import org.apache.catalina.User;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine springTemplateEngine;

    private final UserMapper userMapper;

    @Autowired
    public UserService(JavaMailSender javaMailSender, SpringTemplateEngine springTemplateEngine, UserMapper userMapper) {
        this.javaMailSender = javaMailSender;
        this.springTemplateEngine = springTemplateEngine;
        this.userMapper = userMapper;
    }

    // 전화번호 인증 코드
    public SendRegisterContactCodeResult sendRegisterContactCode(RegisterContactCodeEntity registerContactCode){
        if (registerContactCode == null ||
                registerContactCode.getContact() == null ||
                !registerContactCode.getContact().matches("^(010)(\\d{8})$")){
            return SendRegisterContactCodeResult.FAILURE;
        }
        if (this.userMapper.selectUserByContact(registerContactCode.getContact()) != null){
            return SendRegisterContactCodeResult.FAILURE_DUPLICATE;
        }
        String code = RandomStringUtils.randomNumeric(6);
        String salt = CryptoUtil.hashSha512(String.format("%s%s%f%f",
                registerContactCode.getContact(),
                code,
                Math.random(),
                Math.random()));
        Date createdAt = new Date();
        Date expiresAt = DateUtils.addMinutes(createdAt, 5);
        registerContactCode.setCode(code)
                .setSalt(salt)
                .setCreatedAt(createdAt)
                .setExpiresAt(expiresAt)
                .setExpired(false);
        NCloudUtil.sendSms(registerContactCode.getContact(), String.format("[맛집 회원가입] 인증번호 [%s]를 입력해주세요.",registerContactCode.getCode()));
        return this.userMapper.insertRegisterContactCode(registerContactCode) > 0
                ? SendRegisterContactCodeResult.SUCCESS
                : SendRegisterContactCodeResult.FAILURE;
    }
    public SendRecoverContactCodeResult sendRecoverContactCode(RecoverContactCodeEntity recoverContactCode){
        if (recoverContactCode == null ||
                recoverContactCode.getContact() == null) {
            return SendRecoverContactCodeResult.FAILURE;
        }
        if(this.userMapper.selectUserByContact(recoverContactCode.getContact()) == null){
            return SendRecoverContactCodeResult.FAILURE;
        }
        String code = RandomStringUtils.randomNumeric(6);
        String salt = CryptoUtil.hashSha512(String.format("%s%s%f%f",
                recoverContactCode.getContact(),
                code,
                Math.random(),
                Math.random()));
        Date createdAt = new Date();
        Date expiresAt = DateUtils.addMinutes(createdAt, 5);
                recoverContactCode.setCode(code)
                        .setSalt(salt)
                        .setCreatedAt(createdAt)
                        .setExpiresAt(expiresAt)
                        .setExpired(false);
        NCloudUtil.sendSms(recoverContactCode.getContact(),String.format("[이메일 찾기] 인증번호 [%s]를 입력해주세요",recoverContactCode.getCode()));
        return this.userMapper.insertRecoverContactCode(recoverContactCode) > 0
                ? SendRecoverContactCodeResult.SUCCESS
                : SendRecoverContactCodeResult.FAILURE;
    }

    //=================== 인증 번호 일치 하는지==================================
    public VerifyRegisterContactCodeResult selectRegisterContactCode(RegisterContactCodeEntity registerContactCode) {
        RegisterContactCodeEntity existingRegisterCodeEntity= this.userMapper.selectRegisterContactCode(
                registerContactCode.getContact(),
                registerContactCode.getCode(),
                registerContactCode.getSalt());
        if (existingRegisterCodeEntity == null){
            return VerifyRegisterContactCodeResult.FAILURE;
        }
        if (new Date().compareTo(existingRegisterCodeEntity.getExpiresAt()) > 0) {
            return VerifyRegisterContactCodeResult.FAILURE_EXPIRED;
        }
        existingRegisterCodeEntity.setExpired(true);
        return this.userMapper.updateRegisterContactCode(existingRegisterCodeEntity) > 0
                ? VerifyRegisterContactCodeResult.SUCCESS
                : VerifyRegisterContactCodeResult.FAILURE;
    }
    public VerifyRecoverContactCodeResult verifyRecoverContactCode(RecoverContactCodeEntity recoverContactCode){
        RecoverContactCodeEntity existingRecoverCodeEntity = this.userMapper.selectRecoverContactCodeByContactCodeSalt(
                recoverContactCode.getContact(),
                recoverContactCode.getCode(),
                recoverContactCode.getSalt());
        if (existingRecoverCodeEntity == null) {
            return VerifyRecoverContactCodeResult.FAILURE;
        }
        if(new Date().compareTo(existingRecoverCodeEntity.getExpiresAt())> 0) {
            return VerifyRecoverContactCodeResult.FAILURE_EXPIRED;
        }
        existingRecoverCodeEntity.setExpired(true);
        return this.userMapper.updateRecoverContactCode(existingRecoverCodeEntity) > 0
                ?VerifyRecoverContactCodeResult.SUCCESS
                : VerifyRecoverContactCodeResult.FAILURE;

    }
    public UserEntity getUserByContact(String contact) {
        return this.userMapper.selectUserByContact(contact);
    }
    public SendRecoverEmailCodeResult sendRecoverEmailCode(RecoverEmailCodeEntity recoverEmailCode) throws MessagingException{
        if(recoverEmailCode == null ||
                recoverEmailCode.getEmail() == null){
            return SendRecoverEmailCodeResult.FAILURE;
        }
        if (this.userMapper.selectUserByEmail(recoverEmailCode.getEmail()) == null) {
            return SendRecoverEmailCodeResult.FAILURE;
        }
        recoverEmailCode
                .setCode(RandomStringUtils.randomAlphanumeric(6))
                .setSalt(CryptoUtil.hashSha512(String.format("%s%s%f%f",
                        recoverEmailCode.getCode(),
                        recoverEmailCode.getEmail(),
                        Math.random(),
                        Math.random())))
                .setCreatedAt(new Date())
                .setExpiresAt(DateUtils.addHours(recoverEmailCode.getCreatedAt(), 1))
                .setExpired(false);
        String url = String.format("http://localhost:6795/user/recoverPassword?email=%s&code=%s&salt=%s",
                recoverEmailCode.getEmail(),
                recoverEmailCode.getCode(),
                recoverEmailCode.getSalt());
        Context context = new Context();
        context.setVariable("url", url);

        MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        mimeMessageHelper.setSubject("[맛집 비밀번호 재설정] 이메일 인증");
        mimeMessageHelper.setFrom("tig05205@gmail.com");
        mimeMessageHelper.setTo(recoverEmailCode.getEmail());
        mimeMessageHelper.setText(this.springTemplateEngine.process("_recoverEmail", context), true);
        this.javaMailSender.send(mimeMessage);

        return this.userMapper.insertRecoverEmailCode(recoverEmailCode) > 0
                ? SendRecoverEmailCodeResult.SUCCESS
                : SendRecoverEmailCodeResult.FAILURE;

    }

    public VerifyRecoverEmailCodeResult verifyRecoverEmailCode(RecoverEmailCodeEntity recoverEmailCode){
        if(recoverEmailCode == null ||
                recoverEmailCode.getEmail() == null ||
                recoverEmailCode.getCode() == null ||
                recoverEmailCode.getSalt() == null){
            return VerifyRecoverEmailCodeResult.FAILURE;
        }
        recoverEmailCode = this.userMapper.selectRecoverEmailCodeByEmailCodeSalt(recoverEmailCode);
        if (recoverEmailCode == null) {
            return VerifyRecoverEmailCodeResult.FAILURE;
        }
        if (new Date().compareTo(recoverEmailCode.getExpiresAt()) > 0) {
            return VerifyRecoverEmailCodeResult.FAILURE_EXPIRED;
        }
        recoverEmailCode.setExpired(true);
        return this.userMapper.updateRecoverEmailCode(recoverEmailCode) > 0
                ? VerifyRecoverEmailCodeResult.SUCCESS
                : VerifyRecoverEmailCodeResult.FAILURE;
    }
    public RecoverPasswordResult recoverPassword(RecoverEmailCodeEntity recoverEmailCode, UserEntity user) {
        if(recoverEmailCode == null ||
                recoverEmailCode.getEmail() == null ||
                recoverEmailCode.getCode() == null ||
                recoverEmailCode.getSalt() == null ||
                user == null ||
                user.getPassword() == null) {
            return RecoverPasswordResult.FAILURE;
        }
        recoverEmailCode = this.userMapper.selectRecoverEmailCodeByEmailCodeSalt(recoverEmailCode);
        if (recoverEmailCode == null || !recoverEmailCode.isExpired()) {
            return RecoverPasswordResult.FAILURE;
        }
        user = this.userMapper.selectUserByEmail(user.getEmail());
        if (user == null) {
            return RecoverPasswordResult.FAILURE;
        }
        user.setPassword(CryptoUtil.hashSha512(user.getPassword()));
        return this.userMapper.updateUser(user) > 0 && this.userMapper.deleteRecoverEmailCode(recoverEmailCode) > 0
                ? RecoverPasswordResult.SUCCESS
                : RecoverPasswordResult.FAILURE;


    }

    public CheckEmailResult checkEmail(String email) {
        return this.userMapper.selectUserByEmail(email) == null
                ?CheckEmailResult.OKAY
                :CheckEmailResult.DUPLICATE;
    }
    public CheckNicknameResult checkNickname(String nickname) {
        return this.userMapper.selectUserByNickname(nickname) == null
                ?CheckNicknameResult.SUCCESS
                :CheckNicknameResult.DUPLICATE;
    }
    public RegisterResult register(UserEntity user, RegisterContactCodeEntity registerContactCode) throws MessagingException {
        if(this.userMapper.selectUserByEmail(user.getEmail()) != null){
            return RegisterResult.FAILURE_DUPLICATE_EMAIL;
        }
        if(this.userMapper.selectUserByContact(user.getContact()) != null){
            return RegisterResult.FAILURE_DUPLICATE_CONTACT;
        }
        if (this.userMapper.selectUserByNickname(user.getNickname()) != null){
            return RegisterResult.FAILURE_DUPLICATE_NICKNAME;
        }
        registerContactCode = this.userMapper.selectRegisterContactCode(registerContactCode);
        if (registerContactCode == null || !registerContactCode.isExpired()){
            return RegisterResult.FAILURE;
        }
        user.setPassword(CryptoUtil.hashSha512(user.getPassword()));
        user.setStatus("EMAIL_PENDING");
        user.setAdmin(false); // 보안 관리자 계정으로 만들지 못하게 하기

        RegisterEmailCodeEntity registerEmailCode = new RegisterEmailCodeEntity();
        registerEmailCode.setEmail(user.getEmail());
        registerEmailCode.setCode(RandomStringUtils.randomAlphanumeric(6));
        registerEmailCode.setSalt(CryptoUtil.hashSha512(String.format("%s%s%f%f",
                registerEmailCode.getEmail(),
                registerEmailCode.getCode(),
                Math.random(),
                Math.random())));
        registerEmailCode.setCreatedAt(new Date());
        registerEmailCode.setExpiresAt(DateUtils.addHours(registerEmailCode.getCreatedAt(),1));
        registerEmailCode.setExpired(false);


        String url = String.format("http://localhost:6795/user/emailCode?email=%s&code=%s&salt=%s",
                registerEmailCode.getEmail(),
                registerEmailCode.getCode(),
                registerEmailCode.getSalt());
        Context context = new Context();
        context.setVariable("url", url);
        MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        mimeMessageHelper.setSubject("[맛집 회원가입] 이메일 인증");
        mimeMessageHelper.setFrom("tig05205@gmail.com");
        mimeMessageHelper.setTo(user.getEmail());
        mimeMessageHelper.setText(this.springTemplateEngine.process("_registerEmail", context), true);
        this.javaMailSender.send(mimeMessage);



        return this.userMapper.insertUser(user) > 0 && this.userMapper.insertRegisterEmailCode(registerEmailCode) > 0
                ? RegisterResult.SUCCESS
                : RegisterResult.FAILURE;
    }
    // =========== 이메일 코드 인증 =============================
//    public VerifyRegisterEmailCodeResult verifyRegisterEmailCode(RegisterEmailCodeEntity registerEmailCode, UserEntity user){
//        RegisterEmailCodeEntity existingRegisterEmailCodeEntity = this.userMapper.VerifyRegisterEmailCode(
//                registerEmailCode.getEmail(),
//                registerEmailCode.getCode(),
//                registerEmailCode.getSalt());
//        if(existingRegisterEmailCodeEntity == null){
//            return VerifyRegisterEmailCodeResult.FAILURE;
//        }
//        if(new Date().compareTo(existingRegisterEmailCodeEntity.getExpiresAt()) > 0){
//            return VerifyRegisterEmailCodeResult.FAILURE_EXPIRED;
//        }
//        existingRegisterEmailCodeEntity.setExpired(true);
//        int updateEmailResult = this.userMapper.updateRegisterEmailCode(registerEmailCode);
//        UserEntity confirmUser = this.userMapper.selectUserByEmail(user.getEmail());
//        confirmUser.setStatus("OKAY");
//        int updateUserResult = this.userMapper.updateUser(confirmUser);
//        return updateUserResult > 0 || updateEmailResult > 0
//                ? VerifyRegisterEmailCodeResult.SUCCESS
//                : VerifyRegisterEmailCodeResult.FAILURE;
//    }
//     ======== 쌤 이메일 코드 인증 =============================
    public VerifyRegisterEmailCodeResult verifyRegisterEmailCode(RegisterEmailCodeEntity registerEmailCode){
        if(registerEmailCode.getEmail() == null ||
        registerEmailCode.getCode() == null ||
        registerEmailCode.getSalt() == null ) {
            return VerifyRegisterEmailCodeResult.FAILURE;
        }
        registerEmailCode = this.userMapper.VerifyRegisterEmailCode(registerEmailCode.getEmail(), registerEmailCode.getCode(), registerEmailCode.getSalt());
        if (registerEmailCode == null) {
            return VerifyRegisterEmailCodeResult.FAILURE;
        }
        if (new Date().compareTo(registerEmailCode.getExpiresAt()) > 0) {
            return VerifyRegisterEmailCodeResult.FAILURE_EXPIRED;
        }
        registerEmailCode.setExpired(true);
        UserEntity user = this.userMapper.selectUserByEmail(registerEmailCode.getEmail());
        user.setStatus("OKAY");
        return this.userMapper.updateRegisterEmailCode(registerEmailCode) > 0 && this.userMapper.updateUser(user) > 0
                ? VerifyRegisterEmailCodeResult.SUCCESS
                : VerifyRegisterEmailCodeResult.FAILURE;
    }



    public LoginResult login(UserEntity user){
        UserEntity existingUser = this.userMapper.selectUserByEmail(user.getEmail());
        if(existingUser == null) {
            return LoginResult.FAILURE;
        }
        user.setPassword(CryptoUtil.hashSha512(user.getPassword()));
        if(!user.getPassword().equals(existingUser.getPassword())){
            return LoginResult.FAILURE;
        }
        if(existingUser.getStatus().equals("EMAIL_PENDING")){
            return LoginResult.FAILURE_EMAIL_NOT_VERIFIED;
        }
        if(existingUser.getStatus().equals("DELETED")){
            return LoginResult.FAILURE;
        }
        if(existingUser.getStatus().equals("SUSPENDED")){
            return LoginResult.FAILURE_SUSPENDED;
        }
        if(existingUser.getStatus().equals("OKAY")){
            return LoginResult.SUCCESS;
        }

        user.setNickname(existingUser.getNickname())
                .setContact(existingUser.getContact())
                .setStatus(existingUser.getStatus())
                .setAdmin(existingUser.isAdmin())
                .setRegisteredAt(existingUser.getRegisteredAt());
        return LoginResult.SUCCESS;


    }

}
