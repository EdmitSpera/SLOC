package com.learning.springboot.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.learning.springboot.project.dao.entity.ProjectDo;
import com.learning.springboot.project.dto.req.*;
import com.learning.springboot.project.dto.resp.ProjectIdRespDTO;
import com.learning.springboot.project.dto.resp.ProjectInfoRespDTO;

public interface ProjectService extends IService<ProjectDo> {
    /**
     * 增加项目
     * @param requestParam
     * @return
     */
    void addProject(addProjectReqDTO requestParam);

    /**
     * 删除项目
     * @param requestParam
     */
    void deleteProject(deleteProjectReqDTO requestParam);

    /**
     * 删除项目成员
     * @param requestParam
     */
    void deleteMember(deleteMemberReqDTO requestParam);

    /**
     * 修改项目信息
     * @param requestParam
     */
    void updateProject(updateProjectReqDTO requestParam);

    /**
     * 获取项目Id
     * @param requestParam
     * @return
     */
    ProjectIdRespDTO getProjectId(ProjectIdReqDTO requestParam);

    /**
     * 获取项目信息
     * @param requestParam
     * @return
     */
    ProjectInfoRespDTO getProjectInfo(ProjectInfoReqDTO requestParam);
}
