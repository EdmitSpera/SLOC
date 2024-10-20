package com.learning.springboot.project.dto.req;

import com.learning.springboot.project.dao.entity.ProjectDo;
import com.learning.springboot.project.dao.entity.ProjectMemberDo;
import lombok.Data;

import java.util.List;

@Data
public class ProjectCacheDTO {
    /**
     * 项目信息
     */
    private ProjectDo projectInfo; // 项目信息

    /**
     * 项目成员信息
     */
    private List<ProjectMemberDo> members; // 项目成员信息
}
