package com.learning.springboot.admin.dao.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 协会成员持久层实体
 */
@TableName("sloc_user")
@Data
public class UserDo {
    private Long userId;

    /**
     * 学号
     */
    private String studentNumber;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

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

    /**
     * 注册时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 修改日期
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date modifyTime;

    /**
     * 注销时间
     */
    @TableField(fill = FieldFill.UPDATE)
    private Date deletionTime;

    /**
     * 逻辑删除标识
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private int delFlag;

    /**
     * 管理员标识
     */
    @TableField(fill = FieldFill.INSERT)
    private int adminFlag;
}
