package com.learning.springboot.admin.dto.equipment.req;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateEquipmentReqDTO {
    private String equipmentId;
    /**
     * 装备名称
     */
    private String name;

    /**
     * 类型
     */
    private String type;

    /**
     * 数量
     */
    private Integer sum;

    /**
     * 单件租金
     */
    private BigDecimal rent;

    /**
     * 区
     */
    private String area;

    /**
     * 列
     */
    private String col;

    /**
     * 层
     */
    private String tier;
}
