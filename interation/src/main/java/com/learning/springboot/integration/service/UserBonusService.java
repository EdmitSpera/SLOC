package com.learning.springboot.integration.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.learning.springboot.integration.dao.entity.UserBonusDo;
import com.learning.springboot.integration.dao.entity.UserDetailBonusDo;
import com.learning.springboot.integration.dto.req.UserDetailBonusReqDTO;

public interface UserBonusService extends IService<UserDetailBonusDo> {
    /**
     * 增加用户积分明细
     * @param requestParam
     */
    void addUserDetailBonus(UserDetailBonusReqDTO requestParam);


}
