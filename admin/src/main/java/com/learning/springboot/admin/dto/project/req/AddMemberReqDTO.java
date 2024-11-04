package com.learning.springboot.admin.dto.project.req;

import lombok.Data;

@Data
public class AddMemberReqDTO {
    /**
     * 项目名
     */
    private String projectName;

    /**
     * 成员名字
     */
    private String realName;

    /**
     * 项目角色
     */
    private String roleType;
}
