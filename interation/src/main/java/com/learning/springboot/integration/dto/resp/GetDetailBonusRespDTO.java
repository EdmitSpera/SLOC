package com.learning.springboot.integration.dto.resp;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.learning.springboot.integration.dao.entity.UserDetailBonusDo;
import lombok.Data;

@Data
public class GetDetailBonusRespDTO {
    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 部门
     */
    private String department;

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
