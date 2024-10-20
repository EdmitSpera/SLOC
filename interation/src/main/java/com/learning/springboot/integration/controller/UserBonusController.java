package com.learning.springboot.integration.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.learning.springboot.framework.result.Result;
import com.learning.springboot.framework.result.Results;
import com.learning.springboot.integration.dto.req.*;
import com.learning.springboot.integration.dto.resp.GetBonusRespDTO;
import com.learning.springboot.integration.dto.resp.GetDetailBonusRespDTO;
import com.learning.springboot.integration.dto.resp.GetPageBonusRespDTO;
import com.learning.springboot.integration.service.UserBonusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "积分后管")
@RequiredArgsConstructor
public class UserBonusController {
    private final UserBonusService userBonusService;

    @Operation(summary = "新增用户积分明细")
    @PostMapping("/api/sloc/bonus/addUserBonus")
    public Result<Void> getUserId(@RequestBody UserDetailBonusReqDTO requestParam){
        userBonusService.addUserDetailBonus(requestParam);
        return Results.success();
    }

    @Operation(summary = "删除用户积分明细")
    @PostMapping("/api/sloc/bonus/deleteDetailBonus")
    public Result<Void> deleteDetailBonus(@RequestBody DeleteDetailBonusReqDTO requestParam){
        userBonusService.deleteUserDetailBonus(requestParam);
        return Results.success();
    }

    @Operation(summary = "删除用户总积分")
    @PostMapping("/api/sloc/bonus/deleteBonus")
    public Result<Void> deleteBonus(@RequestBody DeleteBonusReqDTO requestParam){
        userBonusService.deleteUserBonus(requestParam);
        return Results.success();
    }

    @Operation(summary = "获取用户总积分")
    @GetMapping("/api/sloc/bonus/getBonus")
    public Result<GetBonusRespDTO> getBonus(@RequestBody GetBonusReqDTO requestParam){
        return Results.success(userBonusService.getBonus(requestParam));
    }

    @Operation(summary = "获取用户积分明细")
    @GetMapping("/api/sloc/bonus/getDetailBonus")
    public Result<IPage<GetDetailBonusRespDTO>> getDetailBonus(@RequestBody GetDetailBonusReqDTO requestParam){
        return Results.success(userBonusService.getDetailBonus(requestParam));
    }

}
