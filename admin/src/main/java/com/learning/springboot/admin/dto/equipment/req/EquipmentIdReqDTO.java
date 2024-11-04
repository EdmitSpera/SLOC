package com.learning.springboot.admin.dto.equipment.req;

import lombok.Data;

@Data
public class EquipmentIdReqDTO {
    /**
     * 装备名字
     */
    private String name;

    /**
     * 装备类型
     */
    private String type;
}
