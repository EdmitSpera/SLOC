package com.learning.springboot.admin.dto.project.req;

import lombok.Data;

/**
 * 项目id请求实体
 */
@Data
public class ProjectIdReqDTO {
    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 活动类型
     */
    private String type;
}
