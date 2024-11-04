package com.learning.springboot.admin.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.learning.springboot.admin.dao.entity.ProjectDo;
import com.learning.springboot.admin.dto.project.resp.ProjectMemberPageRespDTO;
import com.learning.springboot.admin.dto.project.resp.ProjectPageRespDTO;
import com.learning.springboot.admin.dto.project.req.*;
import com.learning.springboot.framework.dto.CustomPageRespDTO;

public interface ProjectService extends IService<ProjectDo> {
    /**
     * 增加项目
     *
     * @param requestParam
     * @return
     */
    void addProject(addProjectReqDTO requestParam);

    /**
     * 增加项目成员
     * @param requestParam
     */
    void addProjectMember(AddMemberReqDTO requestParam);

    /**
     * 删除项目
     *
     * @param requestParam
     */
    void deleteProject(deleteProjectReqDTO requestParam);

    /**
     * 删除项目成员
     *
     * @param requestParam
     */
    void deleteMember(deleteMemberReqDTO requestParam);

    /**
     * 修改项目信息
     *
     * @param requestParam
     */
    void updateProject(updateProjectReqDTO requestParam);

    /**
     * 修改项目成员信息
     * @param requestParam
     */
    void updateMember(UpdateMemberReqDTO requestParam);

    /**
     * 获取项目分页信息
     */
    CustomPageRespDTO<ProjectPageRespDTO> getProjectPage(String page, String perPage, String type, String status);

    /**
     * 获取项目成员分页
     * @param page
     * @param perPage
     * @param projectName
     * @return
     */
    CustomPageRespDTO<ProjectMemberPageRespDTO> getProjectMemberPage(String page, String perPage, String projectName);


}
