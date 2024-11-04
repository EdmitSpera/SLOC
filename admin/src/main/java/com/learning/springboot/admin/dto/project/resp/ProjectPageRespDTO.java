package com.learning.springboot.admin.dto.project.resp;

import lombok.Data;

@Data
public class ProjectPageRespDTO {
    /**
     * 项目id
     */
    private String projectId;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 项目类型
     */
    private String type;

    /**
     * 项目状态
     */
    private String status;

    /**
     * 项目开展时间
     */
    private String beginTime;

    /**
     * 项目结束时间
     */
    private String endTime;
}
