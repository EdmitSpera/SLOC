package com.learning.springboot.admin.dto.project.resp;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProjectMemberRespDTO {
    /**
     * 用户id
     */
    private Long userId;

    /**
     * 项目角色
     */
    private String roleType;
}
