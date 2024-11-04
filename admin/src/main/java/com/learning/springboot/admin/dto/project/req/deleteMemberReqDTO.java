package com.learning.springboot.admin.dto.project.req;

import lombok.Data;

@Data
public class deleteMemberReqDTO {
    /**
     * 项目id
     */
    private String projectName;

    /**
     * 用户姓名
     */
    private String realName;
}
