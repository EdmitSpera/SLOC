package com.learning.springboot.project.controller;

import com.learning.springboot.framework.result.Result;
import com.learning.springboot.framework.result.Results;
import com.learning.springboot.project.dto.req.*;
import com.learning.springboot.project.dto.resp.ProjectIdRespDTO;
import com.learning.springboot.project.dto.resp.ProjectInfoRespDTO;
import com.learning.springboot.project.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "项目后管")
public class ProjectController {
    private final ProjectService projectService;

    @Operation(summary = "新增项目")
    @PostMapping("/api/sloc/project/addProject")
    public Result<Void> addProject(@RequestBody addProjectReqDTO requestParam){
        projectService.addProject(requestParam);
        return Results.success();
    }

    @Operation(summary = "删除项目")
    @PostMapping("/api/sloc/project/deleteProject")
    public Result<Void> deleteProject(@RequestBody deleteProjectReqDTO requestParam){
        projectService.deleteProject(requestParam);
        return Results.success();
    }

    @Operation(summary = "删除项目成员")
    @PostMapping("/api/sloc/project/deleteMember")
    public Result<Void> deleteMember(@RequestBody deleteMemberReqDTO requestParam){
        projectService.deleteMember(requestParam);
        return Results.success();
    }

    @Operation(summary = "修改项目信息")
    @PostMapping("/api/sloc/project/updateProject")
    public Result<Void> updateProject(@RequestBody updateProjectReqDTO requestParam){
        projectService.updateProject(requestParam);
        return Results.success();
    }

    @Operation(summary = "查找项目id")
    @GetMapping("/api/sloc/project/getProjectId")
    public Result<ProjectIdRespDTO> updateProject(@RequestBody ProjectIdReqDTO requestParam){
        return Results.success(projectService.getProjectId(requestParam));
    }

    @Operation(summary = "查找项目信息")
    @GetMapping("/api/sloc/project/getProjectInfo")
    public Result<ProjectInfoRespDTO> updateProject(@RequestBody ProjectInfoReqDTO requestParam){
        return Results.success(projectService.getProjectInfo(requestParam));
    }

}
