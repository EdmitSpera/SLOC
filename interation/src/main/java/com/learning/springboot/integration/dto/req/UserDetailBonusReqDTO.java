package com.learning.springboot.integration.dto.req;

import com.learning.springboot.integration.common.enums.IntegralInfo;
import lombok.Data;

@Data
public class UserDetailBonusReqDTO {
    /**
     * 用户id
     */
    private String userId;

    /**
     * 积分明细
     */
    private String bonusItem;

}
