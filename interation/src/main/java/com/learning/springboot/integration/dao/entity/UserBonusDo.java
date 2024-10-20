package com.learning.springboot.integration.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

@Data
@TableName("sloc_user_bonus")
public class UserBonusDo {
    /**
     * 总积分id
     */
    private Long summaryId;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 日常活动积分
     */
    private Integer dailyBonus;

    /**
     * 项目活动积分
     */
    private Integer projectBonus;

    /**
     * 总积分
     */
    private Integer totalBonus;

    /**
     * 删除标识
     */
    private int del_flag;
}
