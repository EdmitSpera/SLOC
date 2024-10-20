package com.learning.springboot.integration.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
@TableName("sloc_user_detail_bonus")
public class UserDetailBonusDo {
    /**
     * 积分id
     */
    private Long integrationId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 加分类型
     */
    private String bonusType;

    /**
     * 加分明细
     */
    private String bonusItem;

    /**
     * 分值
     */
    private int points;

    /**
     * 学期封顶
     */
    private int semesterMaxPoints;

    /**
     * 学期信息
     */
    private String academic;

    /**
     * 插入时间
     */
    private Date createTime;

    /**
     * 删除标识
     */
    private int del_flag;
}
