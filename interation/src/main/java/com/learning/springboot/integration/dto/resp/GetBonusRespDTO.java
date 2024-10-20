package com.learning.springboot.integration.dto.resp;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetBonusRespDTO {
    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 部门
     */
    private String department;

    /**
     * 日常活动积分
     */
    private int dailyBonus;

    /**
     * 项目活动积分
     */
    private int projectBonus;

    /**
     * 总积分
     */
    private int totalBonus;
}
