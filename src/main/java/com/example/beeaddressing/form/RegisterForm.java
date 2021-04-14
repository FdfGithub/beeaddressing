package com.example.beeaddressing.form;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class RegisterForm {
    @NotNull(message = "手机号不能为空")
    private String phoneNo;  //手机号

    @NotNull(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码(6-20位)")
    private String password; //密码(6-20位)

    @NotNull(message = "验证码不能为空")
    private String code; //验证码
}
