package com.learning.springboot.admin.dto.project.resp;

import lombok.Data;

@Data
public class ProjectMemberPageRespDTO {
    /**
     * 项目名
     */
    private String projectName;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 部门
     */
    private String department;

    /**
     * 项目角色
     */
    private String roleType;

}
