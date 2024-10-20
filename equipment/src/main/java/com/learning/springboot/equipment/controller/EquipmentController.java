package com.learning.springboot.equipment.controller;

import com.learning.springboot.equipment.dao.entity.EquipmentDo;
import com.learning.springboot.equipment.dto.req.*;
import com.learning.springboot.equipment.dto.resp.EquipmentIdRespDTO;
import com.learning.springboot.equipment.dto.resp.EquipmentInfoRespDTO;
import com.learning.springboot.equipment.service.EquipmentService;
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
}
