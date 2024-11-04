package com.learning.springboot.admin.dto.admin.resp;

import lombok.Data;

@Data
public class LoginRespDTO {
    /**
     * jwt token
     */
    private String token;
}
