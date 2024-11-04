package com.learning.springboot.admin.dto.project.req;

import lombok.Data;

@Data
public class UpdateMemberReqDTO {
    /**
     * 项目id
     */
    private Long projectId;

    /**
     * 姓名
     */
    private String realName;


    /**
     * 职位
     */
    private String roleType;
}
