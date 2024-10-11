package com.learning.springboot.admin.dto.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.learning.springboot.admin.dao.entity.UserDo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 分页查询实体 按年级或部门
 */
@Data
@Schema(description = "分页查询实体，按年级或部门")
public class UserPageReqDTO extends Page<UserDo> {

    /**
     * 年级
     */
    @Schema(description = "年级", example = "2023")
    private Integer grade;

    /**
     * 部门
     */
    @Schema(description = "部门", example = "技术部")
    private String department;
}
