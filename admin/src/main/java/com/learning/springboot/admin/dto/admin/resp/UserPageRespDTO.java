package com.learning.springboot.admin.dto.admin.resp;

import lombok.Data;

import java.util.Date;

/**
 * 分页查询返回传输实体
 */
@Data

public class UserPageRespDTO {
    /**
     * 学号
     */
    private String studentNumber;

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

    /**
     * 校区
     */
    private String campus;

    /**
     * 宿舍
     */
    private String dormitory;

    /**
     * 生日
     */
    private Date birthday;

    /**
     * 民族
     */
    private String nation;

    /**
     * 政治面貌
     */
    private String politicsStatus;
}
