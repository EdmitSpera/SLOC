package com.learning.springboot.admin.dto.interation.req;

import lombok.Data;

@Data
public class UserDetailBonusReqDTO {
    /**
     * 用户id
     */
    private String realName;

    /**
     * 积分明细
     */
    private String bonusItem;

}
