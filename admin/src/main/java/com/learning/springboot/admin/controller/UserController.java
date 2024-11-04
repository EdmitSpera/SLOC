package com.learning.springboot.admin.controller;

import com.learning.springboot.admin.dto.admin.req.registerUserReqDTO;
import com.learning.springboot.admin.dto.admin.req.updateUserAccReqDTO;
import com.learning.springboot.admin.dto.admin.req.updateUserInfoReqDTO;
import com.learning.springboot.admin.dto.admin.resp.UserPageRespDTO;
import com.learning.springboot.admin.service.UserService;
import com.learning.springboot.framework.dto.CustomPageRespDTO;
import com.learning.springboot.framework.result.Result;
import com.learning.springboot.framework.result.Results;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
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
    @PostMapping("/api/sloc/admin/delete/{username}")
    public Result<Void> delete(@PathVariable("username") String username) {
        userService.delete(username);
        return Results.success();
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
            @RequestParam String department,
            @RequestParam String realName
    ) {
        return Results.success(userService.getUserPage(
                page,
                perPage,
                grade,
                department,
                realName
        ));
    }

    @RequestMapping(value = "/api/sloc/admin/getUserPage", method = RequestMethod.OPTIONS)
    public ResponseEntity<?> handleOptions() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Access-Control-Allow-Origin", "http://995020jl23bi.vicp.fun");
        headers.add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        headers.add("Access-Control-Allow-Headers", "*");
        return ResponseEntity.ok().headers(headers).build();
    }


}
