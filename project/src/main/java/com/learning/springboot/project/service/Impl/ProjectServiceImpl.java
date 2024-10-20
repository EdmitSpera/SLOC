package com.learning.springboot.project.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learning.springboot.framework.constants.RedisCacheConstant;
import com.learning.springboot.framework.exception.ClientException;
import com.learning.springboot.framework.exception.ServiceException;
import com.learning.springboot.project.dao.entity.ProjectDo;
import com.learning.springboot.project.dao.entity.ProjectMemberDo;
import com.learning.springboot.project.dao.mapper.ProjectMapper;
import com.learning.springboot.project.dao.mapper.ProjectMemberMapper;
import com.learning.springboot.project.dto.req.*;
import com.learning.springboot.project.dto.resp.ProjectIdRespDTO;
import com.learning.springboot.project.dto.resp.ProjectInfoRespDTO;
import com.learning.springboot.project.dto.resp.ProjectMemberRespDTO;
import com.learning.springboot.project.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.learning.springboot.framework.constants.RedisCacheConstant.Project_KEY;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, ProjectDo> implements ProjectService {

    private final ProjectMemberMapper projectMemberMapper;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addProject(addProjectReqDTO requestParam) {
        try {
            // TODO 改为使用布隆过滤器 判断重复
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
            List<ProjectMemberDo> projectMembers = new ArrayList<>(); // 用于存储所有插入的项目成员信息

            for (ProjectMemberReqDTO memberDto : members) {
                ProjectMemberDo projectMemberDo = new ProjectMemberDo();
                projectMemberDo.setMemberId(IdUtil.getSnowflake().nextId());
                projectMemberDo.setUserId(memberDto.getUserId());
                projectMemberDo.setProjectId(projectId);
                projectMemberDo.setRoleType(memberDto.getRoleType());

                int memberInsertResult = projectMemberMapper.insert(projectMemberDo);
                if (memberInsertResult <= 0) {
                    throw new ClientException("插入异常：项目成员 " + memberDto.getUserId() + " 插入失败");
                }
                // 将成员信息加入到列表中，用于后续缓存
                projectMembers.add(projectMemberDo);
            }

            // 创建封装项目信息和成员信息的对象
            ProjectCacheDTO projectCacheDTO = new ProjectCacheDTO();
            projectCacheDTO.setProjectInfo(projectDo);
            projectCacheDTO.setMembers(projectMembers);

            // 加入缓存
            String projectKey = Project_KEY + projectDo.getProjectName();
            stringRedisTemplate.opsForValue().set(
                    projectKey,
                    JSON.toJSONString(projectCacheDTO),
                    5,
                    TimeUnit.DAYS
            );

        } catch (DataIntegrityViolationException e) {
            throw new ServiceException("插入过程中出现数据完整性异常：" + e.getMessage());
        } catch (Exception e) {
            throw new ServiceException("插入过程中出现未知错误：" + e.getMessage());
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
                    .eq(ProjectDo::getProjectId, requestParam.getProjectId());
            ProjectDo projectDo = baseMapper.selectOne(queryWrapper);

            if (projectDo == null) {
                throw new ClientException("项目不存在或已被删除");
            }

            // 标记项目为删除
            LambdaUpdateWrapper<ProjectDo> updateWrapper = Wrappers.lambdaUpdate(ProjectDo.class)
                    .eq(ProjectDo::getProjectId, requestParam.getProjectId())
                    .set(ProjectDo::getDel_flag, 1);
            int updateProjectRow = baseMapper.update(null, updateWrapper);
            if (updateProjectRow == 0) {
                throw new ServiceException("删除项目失败");
            }

            // 删除项目成员
            LambdaUpdateWrapper<ProjectMemberDo> memberUpdateWrapper = Wrappers.lambdaUpdate(ProjectMemberDo.class)
                    .eq(ProjectMemberDo::getProjectId, requestParam.getProjectId())
                    .set(ProjectMemberDo::getDel_flag, 1);
            int updateMemberRow = projectMemberMapper.update(null, memberUpdateWrapper);
            if (updateMemberRow == 0) {
                throw new ServiceException("删除项目成员失败");
            }

            // 删除缓存中的项目信息
            String projectKey = RedisCacheConstant.Project_KEY + projectDo.getProjectName();
            stringRedisTemplate.delete(projectKey); // 删除项目缓存

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
            // 查询是否存在该项目成员
            LambdaQueryWrapper<ProjectMemberDo> queryWrapper = Wrappers.lambdaQuery(ProjectMemberDo.class)
                    .eq(ProjectMemberDo::getProjectId, requestParam.getProjectId())
                    .eq(ProjectMemberDo::getUserId, requestParam.getUserId())
                    .eq(ProjectMemberDo::getDel_flag, 0);

            ProjectMemberDo projectMemberDo = projectMemberMapper.selectOne(queryWrapper);

            if (projectMemberDo == null) {
                throw new ClientException("该成员不存在或已被删除");
            }

            // 标记该成员为删除
            LambdaUpdateWrapper<ProjectMemberDo> updateWrapper = Wrappers.lambdaUpdate(ProjectMemberDo.class)
                    .eq(ProjectMemberDo::getProjectId, requestParam.getProjectId())
                    .eq(ProjectMemberDo::getUserId, requestParam.getUserId())
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
                    .eq(ProjectDo::getProjectId, requestParam.getProjectId())
                    .eq(ProjectDo::getDel_flag, 0); // 仅查询未被删除的项目
            ProjectDo exist = baseMapper.selectOne(queryWrapper);

            if (exist == null) {
                throw new ClientException("项目不存在或已被删除");
            }

            // 2. 更新项目信息
            LambdaUpdateWrapper<ProjectDo> updateWrapper = Wrappers.lambdaUpdate(ProjectDo.class)
                    .eq(ProjectDo::getProjectId, exist.getProjectId());

            ProjectDo projectDo = ProjectDo.builder()
                    .projectId(requestParam.getProjectId())
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

            // 3. 检查是否有项目成员更新
            List<ProjectMemberReqDTO> members = requestParam.getMembers();
            if (members != null && !members.isEmpty()) {
                // 获取当前数据库中的项目成员信息
                List<ProjectMemberDo> existingMembers = projectMemberMapper.selectList(
                        Wrappers.lambdaQuery(ProjectMemberDo.class)
                                .eq(ProjectMemberDo::getProjectId, projectDo.getProjectId())
                                .eq(ProjectMemberDo::getDel_flag, 0) // 仅查询未删除的成员
                );

                // 原来的项目成员
                Set<Long> existingMemberIds = existingMembers.stream()
                        .map(ProjectMemberDo::getUserId)
                        .collect(Collectors.toSet());

                // 修改的项目成员
                Set<Long> newMemberIds = members.stream()
                        .map(ProjectMemberReqDTO::getUserId)
                        .collect(Collectors.toSet());

                // 3.2 添加新的成员
                for (ProjectMemberReqDTO memberDto : members) {

                    if (!existingMemberIds.contains(memberDto.getUserId())) {
                        // 新增项目成员
                        ProjectMemberDo newMember = new ProjectMemberDo();
                        newMember.setUserId(memberDto.getUserId());
                        newMember.setProjectId(projectDo.getProjectId());
                        newMember.setRoleType(memberDto.getRoleType());
                        projectMemberMapper.insert(newMember);
                    } else {
                        // 修改现有成员信息
                        for (ProjectMemberDo existingMember : existingMembers) {
                            if (existingMember.getUserId().equals(memberDto.getUserId()) &&
                                    !existingMember.getRoleType().equals(memberDto.getRoleType())) {
                                existingMember.setRoleType(memberDto.getRoleType());
                                projectMemberMapper.updateById(existingMember);
                            }
                        }
                    }
                }
            }

            // 4. 删除缓存中的项目信息
            String projectKey = RedisCacheConstant.Project_KEY + projectDo.getProjectName();
            stringRedisTemplate.delete(projectKey);

            // 5. 更新成功后插入新的项目信息到缓存
            stringRedisTemplate.opsForValue().set(
                    projectKey,
                    JSON.toJSONString(projectDo),
                    30,
                    TimeUnit.DAYS
            );
        } catch (Exception e) {
            throw new ServiceException("更新项目过程中出现未知错误：" + e.getMessage());
        }
    }

    @Override
    public ProjectIdRespDTO getProjectId(ProjectIdReqDTO requestParam) {
        ProjectIdRespDTO result = new ProjectIdRespDTO();
        String projectKey = Project_KEY + requestParam.getProjectName();

        // 1. 先查缓存
        String cachedProjectJson = (String) stringRedisTemplate.opsForValue().get(projectKey);

        if (cachedProjectJson != null) {
            // 将JSON字符串转换为ProjectDo对象
            ProjectDo cachedProject = JSON.parseObject(cachedProjectJson, ProjectDo.class);
            result.setProjectId(String.valueOf(cachedProject.getProjectId())); // 设置项目ID
            return result;
        }

        // 2. 如果缓存未命中，则查询数据库
        LambdaQueryWrapper<ProjectDo> queryWrapper = Wrappers.lambdaQuery(ProjectDo.class)
                .eq(ProjectDo::getDel_flag, 0)
                .eq(ProjectDo::getProjectName, requestParam.getProjectName())
                .eq(ProjectDo::getType, requestParam.getType());

        ProjectDo projectDo = baseMapper.selectOne(queryWrapper);

        if (projectDo != null) {
            // 查到项目后返回项目ID
            String foundProjectId = String.valueOf(projectDo.getProjectId());
            result.setProjectId(foundProjectId);

            // 3. 更新缓存
            stringRedisTemplate.opsForValue().set(
                    projectKey,
                    JSON.toJSONString(projectDo),
                    5,
                    TimeUnit.DAYS
            );
            return result;
        }

        // 如果项目不存在
        result.setProjectId(null);
        return result;
    }

    @Override
    public ProjectInfoRespDTO getProjectInfo(ProjectInfoReqDTO requestParam) {
        String projectKey = Project_KEY + requestParam.getProjectName();
        String cachedProjectJson = stringRedisTemplate.opsForValue().get(projectKey);

        // 如果缓存中存在
        if (cachedProjectJson != null) {
            // 将JSON字符串转换为ProjectDo对象
            ProjectCacheDTO cachedProject = JSON.parseObject(cachedProjectJson, ProjectCacheDTO.class);
            // 构造响应对象
            return buildProjectInfoResponse(cachedProject);
        }

        // 查询项目信息
        LambdaQueryWrapper<ProjectDo> projectQueryWrapper = Wrappers.lambdaQuery(ProjectDo.class)
                .eq(ProjectDo::getProjectId, requestParam.getProjectId())
                .eq(ProjectDo::getDel_flag, 0); // 查询未被删除的项目

        ProjectDo projectDo = baseMapper.selectOne(projectQueryWrapper);
        if (projectDo == null) {
            throw new ClientException("项目不存在或已被删除");
        }
        // 查询项目成员信息
        List<ProjectMemberDo> members = projectMemberMapper.selectList(
                Wrappers.lambdaQuery(ProjectMemberDo.class)
                        .eq(ProjectMemberDo::getProjectId, projectDo.getProjectId())
                        .eq(ProjectMemberDo::getDel_flag, 0) // 查询未被删除的成员
        );

        // 构造缓存对象并存入缓存
        ProjectCacheDTO projectCacheDTO = new ProjectCacheDTO();
        projectCacheDTO.setProjectInfo(projectDo);
        projectCacheDTO.setMembers(members);

        stringRedisTemplate.opsForValue().set(
                projectKey,
                JSON.toJSONString(projectCacheDTO),
                5,
                TimeUnit.DAYS // 设置缓存过期时间
        );

        // 5. 返回响应
        return buildProjectInfoResponse(projectCacheDTO);

    }

    private ProjectInfoRespDTO buildProjectInfoResponse(ProjectCacheDTO projectCacheDTO) {
        // 构造项目响应对象
        ProjectDo projectDo = projectCacheDTO.getProjectInfo();
        List<ProjectMemberDo> members = projectCacheDTO.getMembers();

        // 构造成员响应列表
        List<ProjectMemberRespDTO> memberRespList = members.stream()
                .map(member -> new ProjectMemberRespDTO(member.getUserId(), member.getRoleType()))
                .collect(Collectors.toList());

        // 构造并返回项目响应对象
        return ProjectInfoRespDTO.builder()
                .projectName(projectDo.getProjectName())
                .type(projectDo.getType())
                .status(projectDo.getStatus())
                .beginTime(projectDo.getBeginTime())
                .endTime(projectDo.getEndTime())
                .members(memberRespList)
                .build();
    }
}
