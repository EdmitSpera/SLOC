package com.learning.springboot.admin.dto.resp;

import lombok.Data;

@Data
public class UserAccountRespDTO {
    /**
     * 用户账号
     */
    private String username;

    /**
     * 用户密码
     */
    private String password;
}
