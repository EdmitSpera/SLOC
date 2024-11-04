package com.learning.springboot.admin.controller;


import com.learning.springboot.admin.dto.equipment.req.*;
import com.learning.springboot.admin.dto.equipment.resp.EquipmentIdRespDTO;
import com.learning.springboot.admin.dto.equipment.resp.EquipmentInfoRespDTO;
import com.learning.springboot.admin.dto.equipment.resp.EquipmentPageRespDTO;
import com.learning.springboot.admin.service.EquipmentService;
import com.learning.springboot.framework.dto.CustomPageRespDTO;
import com.learning.springboot.framework.result.Result;
import com.learning.springboot.framework.result.Results;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "装备后管")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentService equipmentService;

    @Operation(summary = "新增装备")
    @PostMapping("/api/sloc/equipment/addEquipment")
    public Result<Void> addEquipment(@RequestBody AddEquipmentReqDTO requestParam) {
        equipmentService.addEquipment(requestParam);
        return Results.success();
    }

    @Operation(summary = "删除装备")
    @PostMapping("/api/sloc/equipment/deleteEquipment")
    public Result<Void> deleteEquipment(@RequestBody DeleteEquipmentReqDTO requestParam) {
        equipmentService.deleteEquipment(requestParam);
        return Results.success();
    }

    @Operation(summary = "更新装备")
    @PostMapping("/api/sloc/equipment/updateEquipment")
    public Result<Void> updateEquipment(@RequestBody UpdateEquipmentReqDTO requestParam) {
        equipmentService.updateEquipment(requestParam);
        return Results.success();
    }

    @Operation(summary = "快捷更新装备数量")
    @PostMapping("/api/sloc/equipment/quickUpdateEquipment")
    public Result<Void> quickUpdateEquipment(@RequestBody QuickUpdateEquipmentReqDTO requestParam) {
        equipmentService.quickUpdateEquipment(requestParam);
        return Results.success();
    }

    @Operation(summary = "查询装备信息")
    @GetMapping("/api/sloc/equipment/getEquipmentInfo")
    public Result<EquipmentInfoRespDTO> getEquipmentInfo(@RequestBody EquipmentInfoReqDTO requestParam) {
        return Results.success(equipmentService.getEquipmentInfo(requestParam));
    }

    @Operation(summary = "查询装备id")
    @GetMapping("/api/sloc/equipment/getEquipmentId")
    public Result<EquipmentIdRespDTO> getEquipmentId(@RequestBody EquipmentIdReqDTO requestParam){
        return Results.success(equipmentService.getEquipmentId(requestParam));
    }

    @Operation(summary = "获取装备分页")
    @GetMapping("/api/sloc/equipment/getEquipmentPage")
    public Result<CustomPageRespDTO<EquipmentPageRespDTO>> getBonus(
            @RequestParam String page,
            @RequestParam String perPage,
            @RequestParam String type
    ) {
        return Results.success(equipmentService.getEquipmentPage(page, perPage, type));
    }
}
