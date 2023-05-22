package com.jhkim.matzip.controllers;

import com.jhkim.matzip.entities.*;
import com.jhkim.matzip.enums.*;
import com.jhkim.matzip.services.UserService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.mail.MessagingException;
import javax.print.attribute.standard.Media;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping(value = "/user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }
    @RequestMapping(value = "contactCode" ,
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getContactCode(RegisterContactCodeEntity registerContactCode){
        SendRegisterContactCodeResult result = this.userService.sendRegisterContactCode(registerContactCode);
        JSONObject responseObject = new JSONObject() {{
            put("result", result.name().toLowerCase());
        }};
        if (result == SendRegisterContactCodeResult.SUCCESS) {
            responseObject.put("salt", registerContactCode.getSalt());
        }
        return responseObject.toString();
    }

    @RequestMapping(value = "contactCode",
            method = RequestMethod.PATCH,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String patchContactCode(RegisterContactCodeEntity registerContactCode){
        VerifyRegisterContactCodeResult result = this.userService.selectRegisterContactCode(registerContactCode);
        JSONObject responseObject = new JSONObject() {{  // JSON 형태의 객체 생성
            put("result", result.name().toLowerCase());  // JSON 객체에 "result" 키와 registerVerifyEmail의 이름을 소문자로 바꾼 값을 저장
        }};

        return responseObject.toString(); // 생성한 JSON 객체를 문자열로 변환하여 반환.
    }

    @RequestMapping(value = "emailCount",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getEmailCount(@RequestParam(value = "email")String email) {
        CheckEmailResult result = this.userService.checkEmail(email);
        JSONObject responseObject = new JSONObject(){{
            put("result", result.name().toLowerCase());
        }};
        return responseObject.toString();
    }


    @RequestMapping(value = "nicknameCount",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getNicknameCount(@RequestParam(value = "nickname")String nickname) {
        CheckNicknameResult result = this.userService.checkNickname(nickname);
        JSONObject responseObject = new JSONObject(){{
            put("result", result.name().toLowerCase());
        }};
        return responseObject.toString();
    }

    @RequestMapping(value = "register",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postRegister(UserEntity user, RegisterContactCodeEntity registerContactCode) throws MessagingException {
        RegisterResult result = this.userService.register(user, registerContactCode);
        JSONObject responseObject = new JSONObject() {{
            put("result", result.name().toLowerCase());
        }};
        return responseObject.toString();
    }

    @RequestMapping(value = "login", method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postLogin(HttpSession session,
                            UserEntity user){
        LoginResult result = this.userService.login(user);
        if(result == LoginResult.SUCCESS){
            session.setAttribute("user",user);
        }
        JSONObject responseObject = new JSONObject();
        responseObject.put("result",result.name().toLowerCase());
        return responseObject.toString();
    }

    @RequestMapping(value = "logout", method = RequestMethod.GET)
    public ModelAndView getLogout(HttpSession session){
        session.setAttribute("user", null);
        ModelAndView modelAndView = new ModelAndView("redirect:/");
        return modelAndView;
    }




    @RequestMapping(value = "emailCode",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getEmailCode(RegisterEmailCodeEntity registerEmailCode){
        VerifyRegisterEmailCodeResult result = this.userService.verifyRegisterEmailCode(registerEmailCode);
        ModelAndView modelAndView = new ModelAndView() {{
            setViewName("user/emailCode");
            addObject("result", result.name());
        }};
        return modelAndView;

    }
    @RequestMapping(value = "contactCodeRec",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getContactCodeRec(RecoverContactCodeEntity recoverContactCode){
        SendRecoverContactCodeResult result = this.userService.sendRecoverContactCode(recoverContactCode);
        JSONObject responseObject = new JSONObject() {{
            put("result", result.name().toLowerCase());
        }};
        if (result == SendRecoverContactCodeResult.SUCCESS) {
            responseObject.put("salt",recoverContactCode.getSalt());
        }
        return responseObject.toString();
    }
    @RequestMapping(value = "contactCodeRec",
            method = RequestMethod.PATCH,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String patchContactCodeRec(RecoverContactCodeEntity recoverContactCode){
        VerifyRecoverContactCodeResult result = this.userService.verifyRecoverContactCode(recoverContactCode);
        JSONObject responseObject = new JSONObject() {{
            put("result", result.name().toLowerCase());
        }};
        if (result == VerifyRecoverContactCodeResult.SUCCESS) {
            UserEntity user = this.userService.getUserByContact(recoverContactCode.getContact());
            responseObject.put("email", user.getEmail());
        }
        return responseObject.toString();
    }
    @RequestMapping(value = "recoverPassword",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postRecoverPassword(RecoverEmailCodeEntity recoverEmailCode) throws MessagingException{
        SendRecoverEmailCodeResult result = this.userService.sendRecoverEmailCode(recoverEmailCode);
        JSONObject responseObject = new JSONObject() {{
            put("result", result.name().toLowerCase());
        }};
        return responseObject.toString();
    }

    @RequestMapping(value = "recoverPassword",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getRecoverPassword(RecoverEmailCodeEntity recoverEmailCode) {
        VerifyRecoverEmailCodeResult result = this.userService.verifyRecoverEmailCode(recoverEmailCode);
        ModelAndView modelAndView = new ModelAndView("user/recoverPassword");
        modelAndView.addObject("result", result.name().toLowerCase());
        modelAndView.addObject("recoverEmailCode", recoverEmailCode);
        return modelAndView;
    }

    @RequestMapping(value = "recoverPassword",
            method = RequestMethod.PATCH,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String patchRecoverPassword(RecoverEmailCodeEntity recoverEmailCode, UserEntity user) {
        RecoverPasswordResult result = this.userService.recoverPassword(recoverEmailCode,user);
        JSONObject responseObject = new JSONObject() {{
            put("result", result.name().toLowerCase());
        }};
        return responseObject.toString();
    }









}

