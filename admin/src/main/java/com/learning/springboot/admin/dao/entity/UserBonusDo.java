package com.learning.springboot.admin.dao.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@TableName("sloc_user_bonus")
public class UserBonusDo {
    /**
     * 总积分id
     */
    private Long summaryId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 日常活动积分
     */
    @TableField(fill = FieldFill.INSERT)
    private int dailyBonus;

    /**
     * 项目活动积分
     */
    @TableField(fill = FieldFill.INSERT)
    private int projectBonus;

    /**
     * 总积分
     */
    private int totalBonus;


    /**
     * 删除标识
     */
    private int del_flag;
}
