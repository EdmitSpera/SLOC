package com.learning.springboot.admin.dto.admin.req;

import lombok.Data;

@Data
public class updateUserAccReqDTO {
    /**
     * 用户名
     */
    private String username;

    /**
     * 新密码
     */
    private String password;
}
