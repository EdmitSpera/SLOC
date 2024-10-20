package com.learning.springboot.integration.dto.req;

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
