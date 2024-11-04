package com.learning.springboot.admin.dto.interation.req;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.learning.springboot.admin.dao.entity.UserBonusDo;
import lombok.Data;

/**
 * 分页查询传输实体
 */
@Data
public class GetPageBonusReqDTO extends Page<UserBonusDo> {
    /**
     * 学期
     */
    private String academic;
}

