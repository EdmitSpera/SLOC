package com.learning.springboot.admin.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 项目成员实体
 */
@TableName("sloc_project_member")
@Data
public class ProjectMemberDo {
    @TableId(type = IdType.AUTO)
    /**
     * 主键
     */
    private Long memberId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 项目id
     */
    private Long projectId;

    /**
     * 项目角色
     */
    private String roleType;

    /**
     * 逻辑删除标识
     */
    private int del_flag;
}
