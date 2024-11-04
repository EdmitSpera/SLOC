package com.learning.springboot.admin.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("sloc_equipment")
public class EquipmentDo {
    @TableId(type = IdType.AUTO)
    private Long equipmentId;
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

    /**
     * 删除标识
     */
    private int del_flag;
}
