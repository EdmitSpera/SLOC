package com.learning.springboot.equipment.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learning.springboot.equipment.dao.entity.EquipmentDo;
import com.learning.springboot.equipment.dao.mapper.EquipmentMapper;
import com.learning.springboot.equipment.dto.req.*;
import com.learning.springboot.equipment.dto.resp.EquipmentIdRespDTO;
import com.learning.springboot.equipment.dto.resp.EquipmentInfoRespDTO;
import com.learning.springboot.equipment.service.EquipmentService;
import com.learning.springboot.framework.exception.ClientException;
import com.learning.springboot.framework.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

import static com.learning.springboot.framework.constants.RedisCacheConstant.Equipment_KEY;

@Service
@RequiredArgsConstructor
public class EquipmentServiceImpl extends ServiceImpl<EquipmentMapper, EquipmentDo> implements EquipmentService {

    /**
     * 新增装备
     * @param requestParam
     */
    @Transactional(rollbackFor = Exception.class)
    public void addEquipment(AddEquipmentReqDTO requestParam) {
        try {
            LambdaQueryWrapper<EquipmentDo> queryWrapper = Wrappers.lambdaQuery(EquipmentDo.class)
                    .eq(EquipmentDo::getName, requestParam.getName())
                    .eq(EquipmentDo::getType, requestParam.getType())
                    .eq(EquipmentDo::getDel_flag, 0);

            EquipmentDo exist = baseMapper.selectOne(queryWrapper);
            if (exist != null) {
                throw new ClientException("插入异常：不能重新插入存在过的装备");
            }

            // 插入装备信息
            EquipmentDo equipmentDo = BeanUtil.toBean(requestParam, EquipmentDo.class);
            equipmentDo.setEquipmentId(IdUtil.getSnowflake().nextId());

            int insert = baseMapper.insert(equipmentDo);
            if (insert <= 0) {
                throw new ClientException("插入异常：装备信息插入失败");
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("插入装备过程中出现未知错误：" + e.getMessage());
        }
    }

    /**
     * 逻辑删除装备
     * @param requestParam
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteEquipment(DeleteEquipmentReqDTO requestParam) {
        try {
            LambdaQueryWrapper<EquipmentDo> queryWrapper = Wrappers.lambdaQuery(EquipmentDo.class)
                    .eq(EquipmentDo::getEquipmentId, requestParam.getEquipmentId())
                    .eq(EquipmentDo::getDel_flag, 0);

            EquipmentDo equipmentDo = baseMapper.selectOne(queryWrapper);

            if (equipmentDo == null) {
                throw new ClientException("装备不存在或已被删除");
            }

            LambdaUpdateWrapper<EquipmentDo> updateWrapper = Wrappers.lambdaUpdate(EquipmentDo.class)
                    .eq(EquipmentDo::getEquipmentId, requestParam.getEquipmentId())
                    .set(EquipmentDo::getDel_flag, 1);

            // 删除装备
            int deleteResult = baseMapper.update(null, updateWrapper);
            if (deleteResult == 0) {
                throw new ServiceException("删除装备失败");
            }

        } catch (Exception e) {
            throw new ServiceException("删除装备过程中出现未知错误：" + e.getMessage());
        }
    }

    /**
     * 更新装备信息
     * @param requestParam
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateEquipment(UpdateEquipmentReqDTO requestParam) {
        try {
            LambdaQueryWrapper<EquipmentDo> queryWrapper = Wrappers.lambdaQuery(EquipmentDo.class)
                    .eq(EquipmentDo::getDel_flag, 0)
                    .eq(EquipmentDo::getEquipmentId, requestParam.getEquipmentId());

            EquipmentDo equipmentDo = baseMapper.selectOne(queryWrapper);

            if (equipmentDo == null) {
                throw new ClientException("装备不存在或已被删除");
            }

            BeanUtil.copyProperties(requestParam, equipmentDo);

            int updateResult = baseMapper.updateById(equipmentDo);
            if (updateResult == 0) {
                throw new ServiceException("更新装备失败");
            }

        } catch (Exception e) {
            throw new ServiceException("更新装备过程中出现未知错误：" + e.getMessage());
        }
    }

    /**
     * 获取装备信息
     *
     * @param requestParam
     * @return
     */
    @Override
    public EquipmentInfoRespDTO getEquipmentInfo(EquipmentInfoReqDTO requestParam) {
        LambdaQueryWrapper<EquipmentDo> queryWrapper = Wrappers.lambdaQuery(EquipmentDo.class)
                .eq(EquipmentDo::getDel_flag, 0)
                .eq(EquipmentDo::getEquipmentId, requestParam.getEquipmentId());

        EquipmentDo equipmentDo = baseMapper.selectOne(queryWrapper);

        if (equipmentDo == null) {
            throw new ClientException("装备不存在");
        }


        return BeanUtil.copyProperties(equipmentDo, EquipmentInfoRespDTO.class);
    }

    /**
     * 获取装备ID
     *
     * @param requestParam
     * @return
     */
    @Override
    public EquipmentIdRespDTO getEquipmentId(EquipmentIdReqDTO requestParam) {
        LambdaQueryWrapper<EquipmentDo> queryWrapper = Wrappers.lambdaQuery(EquipmentDo.class)
                .eq(EquipmentDo::getName, requestParam.getName())
                .eq(EquipmentDo::getType, requestParam.getType())
                .eq(EquipmentDo::getDel_flag, 0);

        EquipmentDo exist = baseMapper.selectOne(queryWrapper);
        if (exist == null) {
            throw new ClientException("不存在该装备或已删除");
        }
        EquipmentIdRespDTO result = new EquipmentIdRespDTO();
        result.setEquipmentId(String.valueOf(exist.getEquipmentId()));
        return result;
    }

}
