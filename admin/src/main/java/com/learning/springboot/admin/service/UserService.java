package com.learning.springboot.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.learning.springboot.admin.dao.entity.UserDo;
import com.learning.springboot.admin.dto.admin.req.registerUserReqDTO;
import com.learning.springboot.admin.dto.admin.req.updateUserAccReqDTO;
import com.learning.springboot.admin.dto.admin.req.updateUserInfoReqDTO;
import com.learning.springboot.admin.dto.admin.resp.UserPageRespDTO;
import com.learning.springboot.framework.dto.CustomPageRespDTO;

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
    void delete(String requestParam);


    /**
     * 更新用户账号信息
     *
     * @param requestParam
     */
    void updateUserAccount(updateUserAccReqDTO requestParam);

    /**
     * 更新用户信息
     *
     * @param requestParam
     */
    void updateUserInformation(updateUserInfoReqDTO requestParam);

    /**
     * 获取用户分页信息
     *
     * @param page
     * @param perPage
     * @param grade
     * @param department
     * @return
     */
    CustomPageRespDTO<UserPageRespDTO> getUserPage(String page, String perPage, String grade, String department, String realName);

}
