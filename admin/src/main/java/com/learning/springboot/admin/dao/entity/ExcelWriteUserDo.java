package com.learning.springboot.admin.dao.entity;

import lombok.Data;

import java.util.Date;

@Data
public class ExcelWriteUserDo {
    /**
     * 学号
     */
    private String studentNumber;

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