package com.learning.springboot.admin.controller;


import com.learning.springboot.admin.dto.project.resp.ProjectMemberPageRespDTO;
import com.learning.springboot.admin.dto.project.resp.ProjectPageRespDTO;
import com.learning.springboot.admin.dto.project.req.*;
import com.learning.springboot.admin.service.ProjectService;
import com.learning.springboot.framework.dto.CustomPageRespDTO;
import com.learning.springboot.framework.result.Result;
import com.learning.springboot.framework.result.Results;
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
    public Result<Void> addProject(@RequestBody addProjectReqDTO requestParam) {
        projectService.addProject(requestParam);
        return Results.success();
    }

    @Operation(summary = "新增项目成员")
    @PostMapping("/api/sloc/project/addMember")
    public Result<Void> addProject(@RequestBody AddMemberReqDTO requestParam) {
        projectService.addProjectMember(requestParam);
        return Results.success();
    }

    @Operation(summary = "删除项目")
    @PostMapping("/api/sloc/project/deleteProject")
    public Result<Void> deleteProject(@RequestBody deleteProjectReqDTO requestParam) {
        projectService.deleteProject(requestParam);
        return Results.success();
    }

    @Operation(summary = "删除项目成员")
    @PostMapping("/api/sloc/project/deleteMember")
    public Result<Void> deleteMember(@RequestBody deleteMemberReqDTO requestParam) {
        projectService.deleteMember(requestParam);
        return Results.success();
    }

    @Operation(summary = "修改项目信息")
    @PostMapping("/api/sloc/project/updateProject")
    public Result<Void> updateProject(@RequestBody updateProjectReqDTO requestParam) {
        projectService.updateProject(requestParam);
        return Results.success();
    }

    @Operation(summary = "修改项目成员信息")
    @PostMapping("/api/sloc/project/updateMember")
    public Result<Void> updateMember(@RequestBody UpdateMemberReqDTO requestParam) {
        projectService.updateMember(requestParam);
        return Results.success();
    }


    @Operation(summary = "根据项目类型和状态分页查询")
    @GetMapping("/api/sloc/project/getProjectPage")
    public Result<CustomPageRespDTO<ProjectPageRespDTO>> getProjectPage(
            @RequestParam String page,
            @RequestParam String perPage,
            @RequestParam String type,
            @RequestParam String status) {
        return Results.success(projectService.getProjectPage(
                page,
                perPage,
                type,
                status
        ));
    }

    @Operation(summary = "项目成员分页查询")
    @GetMapping("/api/sloc/project/getProjectMemberPage")
    public Result<CustomPageRespDTO<ProjectMemberPageRespDTO>> getProjectPage(
            @RequestParam String page,
            @RequestParam String perPage,
            @RequestParam String projectName) {
        return Results.success(projectService.getProjectMemberPage(
                page,
                perPage,
                projectName
        ));
    }

}
