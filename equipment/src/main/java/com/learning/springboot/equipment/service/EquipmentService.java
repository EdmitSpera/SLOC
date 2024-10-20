package com.learning.springboot.equipment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.learning.springboot.equipment.dao.entity.EquipmentDo;
import com.learning.springboot.equipment.dto.req.*;
import com.learning.springboot.equipment.dto.resp.EquipmentIdRespDTO;
import com.learning.springboot.equipment.dto.resp.EquipmentInfoRespDTO;

public interface EquipmentService extends IService<EquipmentDo> {

    void addEquipment(AddEquipmentReqDTO requestParam);

    void deleteEquipment(DeleteEquipmentReqDTO requestParam);

    void updateEquipment(UpdateEquipmentReqDTO requestParam);

    EquipmentInfoRespDTO getEquipmentInfo(EquipmentInfoReqDTO requestParam);

    EquipmentIdRespDTO getEquipmentId(EquipmentIdReqDTO requestParam);
}
