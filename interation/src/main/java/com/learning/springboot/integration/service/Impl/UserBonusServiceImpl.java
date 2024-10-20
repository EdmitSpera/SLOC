package com.learning.springboot.integration.service.Impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learning.springboot.framework.exception.ClientException;
import com.learning.springboot.integration.common.enums.BonusPointsEnum;
import com.learning.springboot.integration.dao.entity.UserDetailBonusDo;
import com.learning.springboot.integration.dao.mapper.UserDetailBonusMapper;
import com.learning.springboot.integration.dto.req.UserDetailBonusReqDTO;
import com.learning.springboot.integration.service.UserBonusService;
import com.learning.springboot.integration.util.SemesterUtil;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UserBonusServiceImpl extends ServiceImpl<UserDetailBonusMapper, UserDetailBonusDo> implements UserBonusService {
    private final SemesterUtil semesterUtil = new SemesterUtil();

    @Override
    public void addUserDetailBonus(UserDetailBonusReqDTO requestParam) {
        String bonusItem = requestParam.getBonusItem();
        BonusPointsEnum bonusPointsEnum = Arrays.stream(BonusPointsEnum.values())
                .filter(item -> item.bonusItem().equals(bonusItem))
                .findFirst()
                .orElseThrow(() -> new ClientException("无效的加分项:" + bonusItem));

        // 判断当前学年段
        String currentSemester = SemesterUtil.getCurrentSemester();

        Integer semesterMaxPoints = bonusPointsEnum.semesterMaxPoints();
        // 如果查询出来的项目类型是学期限制分数 查询当前学年度中是否超额
        if(semesterMaxPoints != null){
            LambdaQueryWrapper<UserDetailBonusDo> queryWrapper = Wrappers.lambdaQuery(UserDetailBonusDo.class)
                    .eq(UserDetailBonusDo::getUserId, requestParam.getUserId())
                    .eq(UserDetailBonusDo::getBonusItem, bonusPointsEnum.bonusItem())
                    .eq(UserDetailBonusDo::getAcademic, currentSemester)
                    .eq(UserDetailBonusDo::getDel_flag, 0);

            List<UserDetailBonusDo> existingBonuses = baseMapper.selectList(queryWrapper);

            int totalPoints = existingBonuses.stream().mapToInt(UserDetailBonusDo::getPoints).sum();

            if(totalPoints >= semesterMaxPoints){
                throw new ClientException("当前学期该加分项已达封顶值，无法继续加分");
            }
        }

        UserDetailBonusDo userDetailBonusDo = UserDetailBonusDo.builder()
                .integrationId(IdUtil.getSnowflake().nextId())
                .userId(Long.valueOf(requestParam.getUserId()))
                .bonusType(bonusPointsEnum.bonusType())
                .bonusItem(bonusPointsEnum.bonusItem())
                .points(bonusPointsEnum.points())
                .semesterMaxPoints(bonusPointsEnum.semesterMaxPoints())
                .academic(currentSemester)
                .build();
        int insert = baseMapper.insert(userDetailBonusDo);
    }
}
