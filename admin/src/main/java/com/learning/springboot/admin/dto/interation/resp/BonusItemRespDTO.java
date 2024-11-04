package com.learning.springboot.admin.dto.interation.resp;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BonusItemRespDTO {
    /**
     * 标签
     */
    private String label;

    /**
     * 信息
     */
    private String value;
}
