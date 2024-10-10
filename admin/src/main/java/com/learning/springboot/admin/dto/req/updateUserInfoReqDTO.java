package com.learning.springboot.admin.dto.req;

import lombok.Data;

@Data
public class updateUserInfoReqDTO {
    /**
     * 用户名
     */
    private String username;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 户外ID
     */
    private String nickname;

    /**
     * 性别
     */
    private String gender;

    /**
     * 部门
     */
    private String department;

    /**
     * 年级
     */
    private int grade;

    /**
     * 专业
     */
    private String major;

    /**
     * 校内邮箱
     */
    private String mail;

    /**
     * 电话
     */
    private String phone;

}
