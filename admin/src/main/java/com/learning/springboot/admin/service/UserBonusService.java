package com.learning.springboot.admin.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.learning.springboot.admin.dao.entity.UserDetailBonusDo;
import com.learning.springboot.admin.dto.interation.req.DeleteBonusReqDTO;
import com.learning.springboot.admin.dto.interation.req.DeleteDetailBonusReqDTO;
import com.learning.springboot.admin.dto.interation.req.UserDetailBonusReqDTO;
import com.learning.springboot.admin.dto.interation.resp.BonusItemRespDTO;
import com.learning.springboot.admin.dto.interation.resp.GetBonusRespDTO;
import com.learning.springboot.admin.dto.interation.resp.GetDetailBonusRespDTO;
import com.learning.springboot.framework.dto.CustomPageRespDTO;

import java.util.List;

public interface UserBonusService extends IService<UserDetailBonusDo> {
    /**
     * 增加用户积分明细
     *
     * @param requestParam
     */
    void addUserDetailBonus(UserDetailBonusReqDTO requestParam);

    /**
     * 删除用户积分明细
     *
     * @param requestParam
     */
    void deleteUserDetailBonus(DeleteDetailBonusReqDTO requestParam);

    /**
     * 删除用户总积分
     *
     * @param requestParam
     */
    void deleteUserBonus(DeleteBonusReqDTO requestParam);

    /**
     * 查询用户积分明细分页
     *
     * @param page
     * @param perPage
     * @param userId
     * @param bonusType
     * @param academic
     * @return
     */
    CustomPageRespDTO<GetDetailBonusRespDTO> getDetailBonus(String page, String perPage, String userId, String bonusType, String academic);

    /**
     * 查询用户总积分分页
     * @param page
     * @param perPage
     * @param userId
     * @param grade
     * @param department
     * @return
     */
    CustomPageRespDTO<GetBonusRespDTO> getBonus(String page, String perPage, String grade, String department, String realName);


    /**
     * 获取加分事宜
     * @param bonusType
     * @return
     */
    List<BonusItemRespDTO> getBonusItemsByType(String bonusType);

}
