package com.learning.springboot.integration.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.learning.springboot.integration.dao.entity.UserBonusDo;
import com.learning.springboot.integration.dao.entity.UserDetailBonusDo;
import com.learning.springboot.integration.dto.req.*;
import com.learning.springboot.integration.dto.resp.GetBonusRespDTO;
import com.learning.springboot.integration.dto.resp.GetDetailBonusRespDTO;
import com.learning.springboot.integration.dto.resp.GetPageBonusRespDTO;

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

    /**
     * 查询用户总积分
     * @param requestParam
     * @return
     */
    GetBonusRespDTO getBonus(GetBonusReqDTO requestParam);

    /**
     * 查询用户积分明细分页
     * @param requestParam
     * @return
     */
    IPage<GetDetailBonusRespDTO> getDetailBonus(GetDetailBonusReqDTO requestParam);

}
