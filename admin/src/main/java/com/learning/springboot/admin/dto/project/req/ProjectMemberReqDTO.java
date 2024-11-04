package com.learning.springboot.admin.dto.project.req;

import lombok.Data;

@Data
public class ProjectMemberReqDTO {

    /**
     * 用户真名
     */
    private String realName;

    /**
     * 项目角色
     */
    private String roleType;

}
