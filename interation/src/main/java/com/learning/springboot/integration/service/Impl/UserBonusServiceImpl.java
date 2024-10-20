package com.learning.springboot.integration.service.Impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learning.springboot.framework.exception.ClientException;
import com.learning.springboot.framework.exception.ServiceException;
import com.learning.springboot.integration.common.enums.BonusPointsEnum;
import com.learning.springboot.integration.dao.entity.UserBonusDo;
import com.learning.springboot.integration.dao.entity.UserDetailBonusDo;
import com.learning.springboot.integration.dao.mapper.UserBonusMapper;
import com.learning.springboot.integration.dao.mapper.UserDetailBonusMapper;
import com.learning.springboot.integration.dto.req.UserDetailBonusReqDTO;
import com.learning.springboot.integration.service.UserBonusService;
import com.learning.springboot.integration.util.SemesterUtil;
import lombok.RequiredArgsConstructor;
import net.sf.jsqlparser.util.validation.metadata.DatabaseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserBonusServiceImpl extends ServiceImpl<UserDetailBonusMapper, UserDetailBonusDo> implements UserBonusService {

    private final SemesterUtil semesterUtil = new SemesterUtil();

    private final UserBonusMapper userBonusMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addUserDetailBonus(UserDetailBonusReqDTO requestParam) {
        String bonusItem = requestParam.getBonusItem();
        BonusPointsEnum bonusPointsEnum = Arrays.stream(BonusPointsEnum.values())
                .filter(item -> item.bonusItem().equals(bonusItem))
                .findFirst()
                .orElseThrow(() -> new ClientException("无效的加分项:" + bonusItem));

        // 判断当前学年段
        String currentSemester = SemesterUtil.getCurrentSemester();

        Integer semesterMaxPoints = Optional.ofNullable(bonusPointsEnum.semesterMaxPoints()).orElse(0);

        // 如果查询出来的项目类型是学期限制分数 查询当前学年度中是否超额
        if (semesterMaxPoints != 0) {
            LambdaQueryWrapper<UserDetailBonusDo> queryWrapper = Wrappers.lambdaQuery(UserDetailBonusDo.class)
                    .eq(UserDetailBonusDo::getUserId, requestParam.getUserId())
                    .eq(UserDetailBonusDo::getBonusItem, bonusPointsEnum.bonusItem())
                    .eq(UserDetailBonusDo::getAcademic, currentSemester)
                    .eq(UserDetailBonusDo::getDel_flag, 0);

            List<UserDetailBonusDo> existingBonuses = baseMapper.selectList(queryWrapper);

            int totalPoints = existingBonuses.stream().mapToInt(UserDetailBonusDo::getPoints).sum();

            if (totalPoints >= semesterMaxPoints) {
                throw new ClientException("当前学期该加分项已达封顶值，无法继续加分");
            }
        }

        UserDetailBonusDo userDetailBonusDo = UserDetailBonusDo.builder()
                .integrationId(IdUtil.getSnowflake().nextId())
                .userId(Long.valueOf(requestParam.getUserId()))
                .bonusType(bonusPointsEnum.bonusType())
                .bonusItem(bonusPointsEnum.bonusItem())
                .points(bonusPointsEnum.points())
                .semesterMaxPoints(semesterMaxPoints)
                .academic(currentSemester)
                .build();
        int insert = baseMapper.insert(userDetailBonusDo);
        addUserBonus(userDetailBonusDo);
    }

    /**
     * 将用户积分明细加入到总积分统计中
     */
    @Transactional(rollbackFor = Exception.class)
    public void addUserBonus(UserDetailBonusDo userDetailBonusDo) {
        try {
            LambdaQueryWrapper<UserBonusDo> queryWrapper = Wrappers.lambdaQuery(UserBonusDo.class)
                    .eq(UserBonusDo::getUserId, userDetailBonusDo.getUserId())
                    .eq(UserBonusDo::getDel_flag, 0);

            UserBonusDo existDo = userBonusMapper.selectOne(queryWrapper);
            int points = userDetailBonusDo.getPoints();
            if (existDo == null) {
                // 第一次添加
                UserBonusDo userBonusDo = UserBonusDo.builder()
                        .summaryId(IdUtil.getSnowflake().nextId())
                        .userId(userDetailBonusDo.getUserId())
                        .build();

                if (userDetailBonusDo.getBonusType().equals("日常社团活动")) {
                    userBonusDo.setDailyBonus(points);
                } else if (userDetailBonusDo.getBonusType().equals("项目活动策划")) {
                    userBonusDo.setProjectBonus(points);
                }
                userBonusDo.setTotalBonus(points);
                int insert = userBonusMapper.insert(userBonusDo);
                if (insert != 1) {
                    throw new DatabaseException("更新用户积分记录失败");
                }
            } else {
                // 数据库中如果有 则获取当前的积分信息
                Integer totalBonus = existDo.getTotalBonus();

                // 根据积分明细判断加哪个类型的分
                if (userDetailBonusDo.getBonusType().equals("日常社团活动")) {
                    int dailyBonus = existDo.getDailyBonus() + points;
                    existDo.setDailyBonus(dailyBonus);
                } else if (userDetailBonusDo.getBonusType().equals("项目活动策划")) {
                    int projectBonus = existDo.getProjectBonus() + points;
                    existDo.setProjectBonus(projectBonus);
                }
                // 汇总总积分
                int newTotalBonus = totalBonus + points;
                existDo.setTotalBonus(totalBonus);
                LambdaUpdateWrapper<UserBonusDo> updateWrapper = Wrappers.lambdaUpdate(UserBonusDo.class)
                        .eq(UserBonusDo::getDel_flag, 0)
                        .eq(UserBonusDo::getSummaryId, existDo.getSummaryId());
                int update = userBonusMapper.update(existDo, updateWrapper);
                if (update != 1) {
                    throw new DatabaseException("更新用户积分记录失败");
                }
            }
        } catch (Exception e) {
            // 捕获所有异常，并抛出自定义的运行时异常
            throw new ServiceException("处理用户积分时发生错误: " + e.getMessage());
        }

    }
}
