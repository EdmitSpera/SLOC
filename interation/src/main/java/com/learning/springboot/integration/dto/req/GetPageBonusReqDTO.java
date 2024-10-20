package com.learning.springboot.integration.dto.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.learning.springboot.integration.dao.entity.UserBonusDo;
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

