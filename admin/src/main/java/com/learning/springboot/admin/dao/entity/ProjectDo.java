package com.learning.springboot.admin.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * 项目实体
 */
@TableName("sloc_project")
@Builder
@Data
public class ProjectDo {
    @TableId(type = IdType.AUTO)
    private Long projectId;
    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 项目类型
     */
    private String type;

    /**
     * 项目状态
     */
    private String status;

    /**
     * 项目开展时间
     */
    private Date beginTime;

    /**
     * 项目结束时间
     */
    private Date endTime;

    /**
     * 逻辑删除标识
     */
    private int del_flag;
}
