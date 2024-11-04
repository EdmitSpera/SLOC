package com.learning.springboot.admin.service.Impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learning.springboot.admin.dao.entity.ProjectDo;
import com.learning.springboot.admin.dao.entity.ProjectMemberDo;
import com.learning.springboot.admin.dao.entity.UserDo;
import com.learning.springboot.admin.dao.mapper.ProjectMapper;
import com.learning.springboot.admin.dao.mapper.ProjectMemberMapper;
import com.learning.springboot.admin.dao.mapper.UserMapper;
import com.learning.springboot.admin.dto.project.resp.ProjectMemberPageRespDTO;
import com.learning.springboot.admin.dto.project.resp.ProjectPageRespDTO;
import com.learning.springboot.admin.dto.project.req.*;
import com.learning.springboot.admin.service.ProjectService;
import com.learning.springboot.framework.dto.CustomPageRespDTO;
import com.learning.springboot.framework.exception.ClientException;
import com.learning.springboot.framework.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, ProjectDo> implements ProjectService {

    private final ProjectMemberMapper projectMemberMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addProject(addProjectReqDTO requestParam) {
        try {
            // TODO 改为使用布隆过滤器 判断重复
            // 项目查询
            LambdaQueryWrapper<ProjectDo> lambdaQueryWrapper = Wrappers.lambdaQuery(ProjectDo.class)
                    .eq(ProjectDo::getDel_flag, 0)
                    .eq(ProjectDo::getProjectName, requestParam.getProjectName());

            ProjectDo exist = baseMapper.selectOne(lambdaQueryWrapper);
            if (exist != null) {
                throw new ClientException("不能插入相同的活动，请检查活动名");
            }

            // 插入项目信息
            ProjectDo projectDo = BeanUtil.toBean(requestParam, ProjectDo.class);
            long initProjectId = IdUtil.getSnowflake().nextId();
            projectDo.setProjectId(initProjectId);

            int insert = baseMapper.insert(projectDo);
            if (insert <= 0) {
                throw new ClientException("插入异常：项目信息插入失败");
            }

            // 插入项目成员信息
            Long projectId = projectDo.getProjectId();
            List<ProjectMemberReqDTO> members = requestParam.getMembers();

            for (ProjectMemberReqDTO memberDto : members) {
                LambdaQueryWrapper<UserDo> eq = Wrappers.lambdaQuery(UserDo.class)
                        .eq(UserDo::getRealName, memberDto.getRealName())
                        .eq(UserDo::getDelFlag, 0);

                UserDo userDo = userMapper.selectOne(eq);
                if (userDo == null) {
                    throw new ClientException("无法找到该用户");
                }


                ProjectMemberDo projectMemberDo = new ProjectMemberDo();
                projectMemberDo.setMemberId(IdUtil.getSnowflake().nextId());
                projectMemberDo.setUserId(userDo.getUserId());
                projectMemberDo.setProjectId(projectId);
                projectMemberDo.setRoleType(memberDto.getRoleType());

                int memberInsertResult = projectMemberMapper.insert(projectMemberDo);
                if (memberInsertResult <= 0) {
                    throw new ClientException("插入异常：项目成员 " + userDo.getRealName() + " 插入失败");
                }
            }

        } catch (DataIntegrityViolationException e) {
            throw new ServiceException("插入过程中出现数据完整性异常：" + e.getMessage());
        } catch (Exception e) {
            throw new ServiceException("插入过程中出现未知错误：" + e.getMessage());
        }
    }

    /**
     * 新增项目成员
     */
    @Override
    public void addProjectMember(AddMemberReqDTO requestParam) {
        try {
            // 查询项目是否存在
            LambdaQueryWrapper<ProjectDo> projectDoQueryWrapper = Wrappers.lambdaQuery(ProjectDo.class)
                    .eq(ProjectDo::getProjectName, requestParam.getProjectName())
                    .eq(ProjectDo::getDel_flag, 0);
            ProjectDo projectDo = baseMapper.selectOne(projectDoQueryWrapper);
            if (projectDo == null) {
                throw new ClientException("项目未找到，请检查项目名称是否正确");
            }

            // 查询用户是否存在
            LambdaQueryWrapper<UserDo> userDoQueryWrapper = Wrappers.lambdaQuery(UserDo.class)
                    .eq(UserDo::getRealName, requestParam.getRealName())
                    .eq(UserDo::getDelFlag, 0);
            UserDo userDo = userMapper.selectOne(userDoQueryWrapper);
            if (userDo == null) {
                throw new ClientException("用户未找到，请检查用户姓名是否正确");
            }

            LambdaQueryWrapper<ProjectMemberDo> queryWrapper = Wrappers.lambdaQuery(ProjectMemberDo.class)
                    .eq(ProjectMemberDo::getProjectId, projectDo.getProjectId())
                    .eq(ProjectMemberDo::getUserId, userDo.getUserId());

            ProjectMemberDo memberDo = projectMemberMapper.selectOne(queryWrapper);
            if(memberDo != null){
                throw new ClientException("不能插入重复的项目成员");
            }

            // 构建项目成员实体
            ProjectMemberDo projectMemberDo = new ProjectMemberDo();
            projectMemberDo.setMemberId(IdUtil.getSnowflake().nextId());
            projectMemberDo.setProjectId(projectDo.getProjectId());
            projectMemberDo.setUserId(userDo.getUserId());
            projectMemberDo.setRoleType(requestParam.getRoleType());

            // 插入项目成员记录
            int insert = projectMemberMapper.insert(projectMemberDo);
            if (insert <= 0) {
                throw new ServiceException("新增项目成员失败，数据插入异常");
            }

        } catch (ClientException e) {
            throw e; // 抛出自定义异常
        } catch (Exception e) {
            // 捕获其他未知异常
            throw new ServiceException("新增项目成员时发生未知错误: " + e.getMessage());
        }
    }


    /**
     * 删除项目
     *
     * @param requestParam
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProject(deleteProjectReqDTO requestParam) {
        try {
            // 查询要删除的项目
            LambdaQueryWrapper<ProjectDo> queryWrapper = Wrappers.lambdaQuery(ProjectDo.class)
                    .eq(ProjectDo::getDel_flag, 0)
                    .eq(ProjectDo::getProjectName, requestParam.getProjectName());
            ProjectDo projectDo = baseMapper.selectOne(queryWrapper);

            if (projectDo == null) {
                throw new ClientException("项目不存在或已被删除");
            }

            // 标记项目为删除
            LambdaUpdateWrapper<ProjectDo> updateWrapper = Wrappers.lambdaUpdate(ProjectDo.class)
                    .eq(ProjectDo::getProjectName, requestParam.getProjectName())
                    .set(ProjectDo::getDel_flag, 1);
            int updateProjectRow = baseMapper.update(null, updateWrapper);
            if (updateProjectRow == 0) {
                throw new ServiceException("删除项目失败");
            }

            // 删除项目成员
            LambdaUpdateWrapper<ProjectMemberDo> memberUpdateWrapper = Wrappers.lambdaUpdate(ProjectMemberDo.class)
                    .eq(ProjectMemberDo::getProjectId, projectDo.getProjectId())
                    .set(ProjectMemberDo::getDel_flag, 1);
            int updateMemberRow = projectMemberMapper.update(null, memberUpdateWrapper);
            if (updateMemberRow == 0) {
                throw new ServiceException("删除项目成员失败");
            }


        } catch (Exception e) {
            throw new ServiceException("删除项目过程中出现未知错误：" + e.getMessage());
        }
    }

    /**
     * 删除项目成员
     *
     * @param requestParam
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMember(deleteMemberReqDTO requestParam) {
        try {

            // 查询是否存在当前项目和用户
            LambdaQueryWrapper<ProjectDo> projectDoLambdaQueryWrapper = Wrappers.lambdaQuery(ProjectDo.class)
                    .eq(ProjectDo::getProjectName, requestParam.getProjectName())
                    .eq(ProjectDo::getDel_flag, 0);

            LambdaQueryWrapper<UserDo> userDoLambdaQueryWrapper = Wrappers.lambdaQuery(UserDo.class)
                    .eq(UserDo::getRealName, requestParam.getRealName())
                    .eq(UserDo::getDelFlag, 0);

            ProjectDo projectDo = baseMapper.selectOne(projectDoLambdaQueryWrapper);
            if (projectDo == null) {
                throw new ClientException("项目不存在或已被删除");
            }

            UserDo userDo = userMapper.selectOne(userDoLambdaQueryWrapper);
            if (userDo == null) {
                throw new ClientException("用户未找到，请检查用户姓名是否正确");
            }


            Long projectId = projectDo.getProjectId();
            Long userId = userDo.getUserId();
            // 查询是否存在该项目成员
            LambdaQueryWrapper<ProjectMemberDo> queryWrapper = Wrappers.lambdaQuery(ProjectMemberDo.class)
                    .eq(ProjectMemberDo::getProjectId, projectId)
                    .eq(ProjectMemberDo::getUserId, userId)
                    .eq(ProjectMemberDo::getDel_flag, 0);

            ProjectMemberDo projectMemberDo = projectMemberMapper.selectOne(queryWrapper);

            if (projectMemberDo == null) {
                throw new ClientException("该项目成员不存在或已被删除");
            }

            // 标记该成员为删除
            LambdaUpdateWrapper<ProjectMemberDo> updateWrapper = Wrappers.lambdaUpdate(ProjectMemberDo.class)
                    .eq(ProjectMemberDo::getProjectId, projectId)
                    .eq(ProjectMemberDo::getUserId, userId)
                    .set(ProjectMemberDo::getDel_flag, 1); // 逻辑删除标记

            int updateRow = projectMemberMapper.update(null, updateWrapper);
            if (updateRow == 0) {
                throw new ServiceException("删除项目成员失败");
            }

        } catch (Exception e) {
            throw new ServiceException("删除项目成员过程中出现未知错误：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProject(updateProjectReqDTO requestParam) {
        try {
            // 1. 检查项目是否存在
            LambdaQueryWrapper<ProjectDo> queryWrapper = Wrappers.lambdaQuery(ProjectDo.class)
                    .eq(ProjectDo::getProjectName, requestParam.getProjectName())
                    .eq(ProjectDo::getDel_flag, 0); // 仅查询未被删除的项目
            ProjectDo exist = baseMapper.selectOne(queryWrapper);

            if (exist == null) {
                throw new ClientException("项目不存在或已被删除");
            }

            // 2. 更新项目信息
            LambdaUpdateWrapper<ProjectDo> updateWrapper = Wrappers.lambdaUpdate(ProjectDo.class)
                    .eq(ProjectDo::getProjectId, exist.getProjectId());

            ProjectDo projectDo = ProjectDo.builder()
                    .projectId(exist.getProjectId())
                    .projectName(requestParam.getProjectName())
                    .type(requestParam.getType())
                    .status(requestParam.getStatus())
                    .beginTime(requestParam.getBeginTime())
                    .endTime(requestParam.getEndTime())
                    .build();

            int updateProjectRow = baseMapper.update(projectDo, updateWrapper);
            if (updateProjectRow == 0) {
                throw new ServiceException("更新项目失败，请稍后重试");
            }

        } catch (Exception e) {
            throw new ServiceException("更新项目过程中出现未知错误：" + e.getMessage());
        }
    }

    @Override
    public void updateMember(UpdateMemberReqDTO requestParam) {
        LambdaQueryWrapper<UserDo> userDoLambdaQueryWrapper = Wrappers.lambdaQuery(UserDo.class)
                .eq(UserDo::getRealName, requestParam.getRealName())
                .eq(UserDo::getDelFlag, 0);

        UserDo userDo = userMapper.selectOne(userDoLambdaQueryWrapper);
        if (userDo == null) {
            throw new ClientException("用户未找到，请检查用户姓名是否正确");
        }

        // 查询出需要修改的项目用户
        LambdaQueryWrapper<ProjectMemberDo> queryWrapper = Wrappers.lambdaQuery(ProjectMemberDo.class)
                .eq(ProjectMemberDo::getProjectId, requestParam.getProjectId())
                .eq(ProjectMemberDo::getUserId, userDo.getUserId())
                .eq(ProjectMemberDo::getDel_flag, 0);

        ProjectMemberDo projectMemberDo = projectMemberMapper.selectOne(queryWrapper);
        if(projectMemberDo == null){
            throw new ClientException("当前项目成员已删除或不存在");
        }

        LambdaUpdateWrapper<ProjectMemberDo> lambdaUpdateWrapper = Wrappers.lambdaUpdate(ProjectMemberDo.class)
                .eq(ProjectMemberDo::getProjectId, requestParam.getProjectId())
                .eq(ProjectMemberDo::getUserId, userDo.getUserId())
                .set(ProjectMemberDo::getRoleType, requestParam.getRoleType());

        int update = projectMemberMapper.update(projectMemberDo, lambdaUpdateWrapper);
        if (update == 0) {
            throw new ServiceException("更新项目失败，请稍后重试");
        }
    }

    /**
     * 分页查询项目
     * @param page
     * @param perPage
     * @param type
     * @param status
     * @return
     */
    @Override
    public CustomPageRespDTO<ProjectPageRespDTO> getProjectPage(String page, String perPage, String type, String status) {
        try {
            // 构造分页
            long curPage = Long.parseLong(page);
            long curPerPage = Long.parseLong(perPage);
            Page<ProjectDo> newPage = new Page<>(curPage, curPerPage);

            // 查询条件
            LambdaQueryWrapper<ProjectDo> queryWrapper = Wrappers.lambdaQuery(ProjectDo.class)
                    .eq(ProjectDo::getDel_flag, 0);
            if (!Objects.equals(type, "")) {
                queryWrapper.eq(ProjectDo::getType, type);
            }
            if (!Objects.equals(status, "")) {
                queryWrapper.eq(ProjectDo::getStatus, status);
            }

            // 分页查询项目信息
            Page<ProjectDo> projectDoPage = baseMapper.selectPage(newPage, queryWrapper);
            if (projectDoPage == null || projectDoPage.getRecords().isEmpty()) {
                throw new ClientException("未找到项目信息");
            }

            // 日期格式化
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

            // 转换分页结果并格式化时间
            IPage<ProjectPageRespDTO> pageResult = projectDoPage.convert(each -> {
                ProjectPageRespDTO dto = BeanUtil.toBean(each, ProjectPageRespDTO.class);

                // 格式化 beginTime 和 endTime
                if (each.getBeginTime() != null) {
                    dto.setBeginTime(dateFormatter.format(each.getBeginTime()));
                }
                if (each.getEndTime() != null) {
                    dto.setEndTime(dateFormatter.format(each.getEndTime()));
                }

                return dto;
            });

            // 返回分页响应
            return new CustomPageRespDTO<>(
                    pageResult.getRecords(),
                    pageResult.getTotal(),
                    pageResult.getSize(),
                    pageResult.getCurrent(),
                    pageResult.getPages()
            );

        } catch (ClientException e) {
            // 捕获自定义异常
            throw e;
        } catch (Exception e) {
            // 捕获其他未知异常
            throw new ServiceException("查询项目时发生未知错误: " + e.getMessage());
        }
    }



    @Override
    public CustomPageRespDTO<ProjectMemberPageRespDTO> getProjectMemberPage(String page, String perPage, String projectName) {
        try {
            if(projectName.isEmpty()){   // TODO 硬编码 后期修复初始不传user_id的bug
                return new CustomPageRespDTO<>(null,0,0,0,0);
            }

            // 构造分页
            long curPage = Long.parseLong(page);
            long curPerPage = Long.parseLong(perPage);
            Page<ProjectMemberDo> newPage = new Page<>(curPage, curPerPage);

            LambdaQueryWrapper<ProjectDo> projectDoLambdaQueryWrapper = Wrappers.lambdaQuery(ProjectDo.class)
                    .eq(ProjectDo::getProjectName, projectName)
                    .eq(ProjectDo::getDel_flag, 0);
            ProjectDo projectDo = baseMapper.selectOne(projectDoLambdaQueryWrapper);
            if(projectDo == null){
                throw new ClientException("当前项目已删除或不存在");
            }

            LambdaQueryWrapper<ProjectMemberDo> memberQueryWrapper = Wrappers.lambdaQuery(ProjectMemberDo.class)
                    .eq(ProjectMemberDo::getDel_flag, 0)
                    .eq(ProjectMemberDo::getProjectId, projectDo.getProjectId());

            Page<ProjectMemberDo> projectMemberDoPage = projectMemberMapper.selectPage(newPage, memberQueryWrapper);

            if (projectMemberDoPage == null || projectMemberDoPage.getRecords().isEmpty()) {
                throw new ClientException("未找到项目信息");
            }



            // 转换项目成员并过滤掉 userDo 为空的记录
            List<ProjectMemberPageRespDTO> filteredRecords = projectMemberDoPage.getRecords().stream()
                    .map(each -> {
                        LambdaQueryWrapper<UserDo> eq = Wrappers.lambdaQuery(UserDo.class)
                                .eq(UserDo::getUserId, each.getUserId())
                                .eq(UserDo::getDelFlag, 0);
                        UserDo userDo = userMapper.selectOne(eq);

                        if (userDo == null) {
                            LambdaUpdateWrapper<ProjectMemberDo> updateWrapper = Wrappers.lambdaUpdate(ProjectMemberDo.class)
                                    .eq(ProjectMemberDo::getProjectId, projectDo.getProjectId())
                                    .eq(ProjectMemberDo::getUserId, each.getUserId())
                                    .set(ProjectMemberDo::getDel_flag, 0);
                            projectMemberMapper.update(null, updateWrapper);
                            return null;
                        }

                        ProjectMemberPageRespDTO dto = BeanUtil.toBean(each, ProjectMemberPageRespDTO.class);
                        dto.setProjectName(projectName);
                        dto.setRealName(userDo.getRealName());
                        dto.setDepartment(userDo.getDepartment());
                        return dto;
                    })
                    .filter(Objects::nonNull) // 过滤掉 null 的记录
                    .collect(Collectors.toList());

            // 返回分页响应
            return new CustomPageRespDTO<>(
                    filteredRecords,
                    projectMemberDoPage.getTotal(),
                    projectMemberDoPage.getSize(),
                    projectMemberDoPage.getCurrent(),
                    projectMemberDoPage.getPages()
            );

        } catch (ClientException e) {
            // 捕获自定义异常
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            // 捕获其他未知异常
            throw new ServiceException("查询项目时发生未知错误: " + e.getMessage());
        }
    }
}
