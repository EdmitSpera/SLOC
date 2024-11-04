package com.learning.springboot.admin.dto.interation.req;

import lombok.Data;

@Data
public class DeleteDetailBonusReqDTO {
    /**
     * 用户Id
     */
    private String userId;

    /**
     * 积分明细
     */
    private String bonusItem;
}
