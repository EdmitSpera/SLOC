package com.learning.springboot.admin.dto.project.req;

import lombok.Data;

@Data
public class ProjectInfoReqDTO {
    /**
     * 项目Id
     */
    private String projectId;

    /**
     * 项目名
     */
    private String projectName;
}
