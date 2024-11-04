package com.learning.springboot.admin.dto.interation.resp;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetBonusRespDTO {
    private String userId;
    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 年级
     */
    private int grade;

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
