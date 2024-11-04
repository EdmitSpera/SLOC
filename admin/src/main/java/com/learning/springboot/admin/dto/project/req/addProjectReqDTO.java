package com.learning.springboot.admin.dto.project.req;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class addProjectReqDTO {
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
     * 项目成员信息
     */
    private List<ProjectMemberReqDTO> members;

    /**
     * 项目开展时间
     */
    private Date beginTime;

    /**
     * 项目结束时间
     */
    private Date endTime;
}
