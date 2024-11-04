package com.learning.springboot.admin.dto.interation.resp;

import lombok.Data;

@Data
public class GetDetailBonusRespDTO {
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
     * 学期信息
     */
    private String academic;
}
