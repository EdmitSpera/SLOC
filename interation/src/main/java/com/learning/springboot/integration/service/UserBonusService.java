package com.learning.springboot.integration.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.learning.springboot.integration.dao.entity.UserBonusDo;
import com.learning.springboot.integration.dao.entity.UserDetailBonusDo;
import com.learning.springboot.integration.dto.req.DeleteBonusReqDTO;
import com.learning.springboot.integration.dto.req.DeleteDetailBonusReqDTO;
import com.learning.springboot.integration.dto.req.UserDetailBonusReqDTO;

public interface UserBonusService extends IService<UserDetailBonusDo> {
    /**
     * 增加用户积分明细
     * @param requestParam
     */
    void addUserDetailBonus(UserDetailBonusReqDTO requestParam);

    /**
     * 删除用户积分明细
     * @param requestParam
     */
    void deleteUserDetailBonus(DeleteDetailBonusReqDTO requestParam);

    /**
     * 删除用户总积分
     * @param requestParam
     */
    void deleteUserBonus(DeleteBonusReqDTO requestParam);
}
