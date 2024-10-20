package com.learning.springboot.admin.dto.req;

import lombok.Data;

/**
 *
 */
@Data
public class UserIdReqDTO {
    /**
     * 账号
     */
    private String username;
    /**
     * 学号
     */
    private String studentNumber;
}
