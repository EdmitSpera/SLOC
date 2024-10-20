package com.learning.springboot.integration.dto.resp;

import lombok.Data;

@Data
public class GetPageBonusRespDTO {
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
