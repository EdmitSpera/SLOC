package com.learning.springboot.admin.controller;

import com.learning.springboot.admin.dto.admin.req.LoginReqDTO;
import com.learning.springboot.admin.dto.admin.resp.LoginRespDTO;
import com.learning.springboot.admin.service.LoginService;
import com.learning.springboot.framework.result.Result;
import com.learning.springboot.framework.result.Results;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "登录模块")
@RequiredArgsConstructor
public class AuthController {

    private final LoginService loginService;

    @Operation(summary = "登录")
    @PostMapping("/api/sloc/admin/login")
    public Result<LoginRespDTO> login(@RequestBody LoginReqDTO requestParam, HttpServletResponse response) {
        return Results.success(loginService.login(requestParam, response));
    }
}
