package com.learning.springboot.admin.dto.admin.req;

import lombok.Data;

@Data
public class LoginReqDTO {
    /**
     * 账号
     */
    private String username;

    /**
     * 密码
     */
    private String password;
}
