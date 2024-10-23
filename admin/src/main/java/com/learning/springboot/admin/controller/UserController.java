package com.learning.springboot.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.learning.springboot.admin.dto.req.*;
import com.learning.springboot.admin.dto.resp.*;
import com.learning.springboot.admin.service.UserService;
import com.learning.springboot.framework.result.Result;
import com.learning.springboot.framework.result.Results;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@Tag(name = "用户后管")
public class UserController {
    private final UserService userService;

    @Operation(summary = "注册用户")
    @PostMapping("/api/sloc/admin/register")
    public Result<Void> register(@RequestBody registerUserReqDTO requestParam) {
        userService.register(requestParam);
        return Results.success();
    }

    @Operation(summary = "根据Excel注册用户")
    @GetMapping("/api/sloc/admin/excelRegister")
    public Result<Void> registerUsersExcel(@RequestParam String filePath) {
        userService.registerUsersExcel(filePath);
        return Results.success();
    }

    @Operation(summary = "根据年级信息写Excel")
    @GetMapping("/api/sloc/admin/excelWriter")
    public Result<Void> writeExcelBasedDatabase(@RequestParam int grade, @RequestParam String filePath) {
        userService.writeExcelBasedDatabase(grade, filePath);
        return Results.success();
    }

    @Operation(summary = "删除用户")
    @PostMapping("/api/sloc/admin/delete")
    public Result<Void> delete(@RequestBody deleteUserReqDTO requestParam) {
        userService.delete(requestParam);
        return Results.success();
    }

    @Operation(summary = "根据学号获取账号信息")
    @GetMapping("/api/sloc/admin/getUserAccount")
    public Result<UserAccountRespDTO> getUserAccount(@RequestBody UserAccountReqDTO requestParam) {
        return Results.success(userService.getUserAccount(requestParam));
    }

    @Operation(summary = "根据真实姓名获取用户信息")
    @GetMapping("/api/sloc/admin/getUserInformation")
    public Result<UserInformationRespDTO> getUserInformation(@RequestBody UserInformationReqDTO requestParam) {
        return Results.success(userService.getUserInformation(requestParam));
    }

    @Operation(summary = "根据账号修改密码")
    @PostMapping("/api/sloc/admin/updateUserAccount")
    public Result<Void> updateUserAccount(@RequestBody updateUserAccReqDTO requestParam) {
        userService.updateUserAccount(requestParam);
        return Results.success();
    }

    @Operation(summary = "根据账号修改信息")
    @PostMapping("/api/sloc/admin/updateUserInformation")
    public Result<Void> updateUserInformation(@RequestBody updateUserInfoReqDTO requestParam) {
        userService.updateUserInformation(requestParam);
        return Results.success();
    }

    @Operation(summary = "根据年级或部门分页查询")
    @GetMapping("/api/sloc/admin/getUserPage")
    public Result<CustomPageRespDTO<UserPageRespDTO>> getUserPage(
            @RequestParam String page,
            @RequestParam String perPage,
            @RequestParam String grade,
            @RequestParam String department) {
        IPage<UserPageRespDTO> pageResult = userService.getUserPage(
                page,
                perPage,
                grade,
                department
        );
        CustomPageRespDTO<UserPageRespDTO> customResponse = new CustomPageRespDTO<UserPageRespDTO>(
                pageResult.getRecords(),
                pageResult.getTotal(),
                pageResult.getSize(),
                pageResult.getCurrent(),
                pageResult.getPages()
        );
        return Results.success(customResponse);
    }

    @Operation(summary = "根据学号查询用户id")
    @GetMapping("/api/sloc/admin/getUserId")
    public Result<UserIdRespDTO> getUserId(@RequestBody UserIdReqDTO requestParam) {
        return Results.success(userService.getUserId(requestParam));
    }
}
