package com.learning.springboot.admin.controller;


import com.learning.springboot.admin.dto.interation.req.DeleteBonusReqDTO;
import com.learning.springboot.admin.dto.interation.req.DeleteDetailBonusReqDTO;
import com.learning.springboot.admin.dto.interation.req.UserDetailBonusReqDTO;
import com.learning.springboot.admin.dto.interation.resp.BonusItemRespDTO;
import com.learning.springboot.admin.dto.interation.resp.GetBonusRespDTO;
import com.learning.springboot.admin.dto.interation.resp.GetDetailBonusRespDTO;
import com.learning.springboot.admin.service.UserBonusService;
import com.learning.springboot.framework.dto.CustomPageRespDTO;
import com.learning.springboot.framework.result.Result;
import com.learning.springboot.framework.result.Results;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "积分后管")
@RequiredArgsConstructor
public class UserBonusController {
    private final UserBonusService userBonusService;

    @Operation(summary = "新增用户积分明细")
    @PostMapping("/api/sloc/bonus/addUserBonus")
    public Result<Void> getUserId(@RequestBody UserDetailBonusReqDTO requestParam) {
        userBonusService.addUserDetailBonus(requestParam);
        return Results.success();
    }

    @Operation(summary = "删除用户积分明细")
    @PostMapping("/api/sloc/bonus/deleteDetailBonus")
    public Result<Void> deleteDetailBonus(@RequestBody DeleteDetailBonusReqDTO requestParam) {
        userBonusService.deleteUserDetailBonus(requestParam);
        return Results.success();
    }

    @Operation(summary = "删除用户总积分")
    @PostMapping("/api/sloc/bonus/deleteBonus")
    public Result<Void> deleteBonus(@RequestBody DeleteBonusReqDTO requestParam) {
        userBonusService.deleteUserBonus(requestParam);
        return Results.success();
    }


    @Operation(summary = "获取用户积分明细分页")
    @GetMapping("/api/sloc/bonus/getDetailBonus")
    public Result<CustomPageRespDTO<GetDetailBonusRespDTO>> getDetailBonus(
            @RequestParam String page,
            @RequestParam String perPage,
            @RequestParam String userId,
            @RequestParam String bonusType,
            @RequestParam String academic
    ) {
        return Results.success(userBonusService.getDetailBonus(page, perPage, userId, bonusType, academic));
    }

    @Operation(summary = "获取用户总积分分页")
    @GetMapping("/api/sloc/bonus/getBonus")
    public Result<CustomPageRespDTO<GetBonusRespDTO>> getBonus(
            @RequestParam String page,
            @RequestParam String perPage,
            @RequestParam String grade,
            @RequestParam String department,
            @RequestParam String realName
    ) {
        return Results.success(userBonusService.getBonus(page, perPage, grade, department,realName));
    }

    @Operation(summary = "获取加分明细")
    @GetMapping("/api/sloc/bonus/getBonusItems")
    public Result<List<BonusItemRespDTO>> getBonusItems(@RequestParam String bonusType) {
        // 根据 bonusType 过滤枚举项
        return Results.success(userBonusService.getBonusItemsByType(bonusType));
    }
}
