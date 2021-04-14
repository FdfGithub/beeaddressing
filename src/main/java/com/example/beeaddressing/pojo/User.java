package com.example.beeaddressing.pojo;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@Table(name = "bee_user")
public class User {
    @Id
    private Integer userId; //用户ID

    private String userName; //用户名

    private String userTel; //手机号

    private String userHeadUrl; //头像地址

    private String userPwd; //用户密码

    private LocalDateTime timestamp;
}
