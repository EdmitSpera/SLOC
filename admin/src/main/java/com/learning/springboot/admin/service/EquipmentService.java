package com.learning.springboot.admin.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.learning.springboot.admin.dao.entity.EquipmentDo;
import com.learning.springboot.admin.dto.equipment.req.*;
import com.learning.springboot.admin.dto.equipment.resp.EquipmentIdRespDTO;
import com.learning.springboot.admin.dto.equipment.resp.EquipmentInfoRespDTO;
import com.learning.springboot.admin.dto.equipment.resp.EquipmentPageRespDTO;
import com.learning.springboot.framework.dto.CustomPageRespDTO;

public interface EquipmentService extends IService<EquipmentDo> {

    void addEquipment(AddEquipmentReqDTO requestParam);

    void deleteEquipment(DeleteEquipmentReqDTO requestParam);

    void updateEquipment(UpdateEquipmentReqDTO requestParam);

    void quickUpdateEquipment(QuickUpdateEquipmentReqDTO requestParam);

    EquipmentInfoRespDTO getEquipmentInfo(EquipmentInfoReqDTO requestParam);

    EquipmentIdRespDTO getEquipmentId(EquipmentIdReqDTO requestParam);

    CustomPageRespDTO<EquipmentPageRespDTO> getEquipmentPage(String page, String perPage, String type);


}
