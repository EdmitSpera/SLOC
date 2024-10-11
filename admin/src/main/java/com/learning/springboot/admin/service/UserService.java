package com.learning.springboot.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.learning.springboot.admin.dao.entity.UserDo;
import com.learning.springboot.admin.dto.req.*;
import com.learning.springboot.admin.dto.resp.UserAccountRespDTO;
import com.learning.springboot.admin.dto.resp.UserInformationRespDTO;
import com.learning.springboot.admin.dto.resp.UserPageRespDTO;

public interface UserService extends IService<UserDo> {
    /**
     * 新增用户
     */
    void register(registerUserReqDTO requestParam);

    /**
     * 根据Excel导入用户
     */
    void registerUsersExcel(String filePath);

    /**
     * 按照年级写表格
     *
     * @param grade
     * @param filePath
     */
    void writeExcelBasedDatabase(int grade, String filePath);


    /**
     * 删除用户
     *
     * @param requestParam
     */
    void delete(deleteUserReqDTO requestParam);

    /**
     * 根据学号获取账号信息
     *
     * @param requestParam 学号
     * @return 账号信息
     */
    UserAccountRespDTO getUserAccount(UserAccountReqDTO requestParam);

    /**
     * 根据真实姓名获取用户信息
     *
     * @param requestParam 真名
     * @return 用户信息
     */
    UserInformationRespDTO getUserInformation(UserInformationReqDTO requestParam);

    /**
     *
     * @param requestParam
     */
    void updateUserAccount(updateUserAccReqDTO requestParam);

    /**
     *
     * @param requestParam
     */
    void updateUserInformation(updateUserInfoReqDTO requestParam);

    /**
     *
     * @param requestParam
     * @return
     */
    IPage<UserPageRespDTO> getUserPage(UserPageReqDTO requestParam);

}
