package com.learning.springboot.project.dto.req;

import lombok.Data;

@Data
public class ProjectMemberReqDTO {
    /**
     * 用户id
     */
    private Long userId;

    /**
     * 项目角色
     */
    private String roleType;

}
