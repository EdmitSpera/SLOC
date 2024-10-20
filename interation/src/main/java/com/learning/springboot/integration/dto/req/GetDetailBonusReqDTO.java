package com.learning.springboot.integration.dto.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.learning.springboot.integration.dao.entity.UserDetailBonusDo;
import lombok.Data;

@Data
public class GetDetailBonusReqDTO extends Page<UserDetailBonusDo> {

    /**
     * 用户id
     */
    private String userId;
}
