package com.example.beeaddressing.controller;

import com.example.beeaddressing.common.Response;
import com.example.beeaddressing.form.RegisterForm;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Controller
@RequestMapping("/user")
public class UserController {
    /**
     * 请求注册手机验证码
     * @param PhoneNo 手机号
     * @return
     */
    @GetMapping("/sendValidateCode")
    public Response sendValidateCode(String PhoneNo){
        return null;
    }


    /**
     * 注册
     * @param form 注册信息
     * @return
     */
    @PostMapping("/register")
    public Response register(@Valid RegisterForm form,Errors error){
        return null;
    }


    /**
     * 登录
     * @param phoneNo 手机号
     * @param password 密码
     * @return
     */
    @PostMapping("/login")
    public Response login(@NotNull String phoneNo, String password){
        return null;
    }
}
