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
import com.learning.springboot.integration.dto.req.DeleteBonusReqDTO;
import com.learning.springboot.integration.dto.req.DeleteDetailBonusReqDTO;
import com.learning.springboot.integration.dto.req.UserDetailBonusReqDTO;
import com.learning.springboot.integration.service.UserBonusService;
import com.learning.springboot.integration.util.SemesterUtil;
import lombok.RequiredArgsConstructor;
import net.sf.jsqlparser.util.validation.metadata.DatabaseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionException;
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
     * 删除积分明细
     * @param requestParam
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteUserDetailBonus(DeleteDetailBonusReqDTO requestParam) {
        try {
            String bonusItem = requestParam.getBonusItem();
            BonusPointsEnum bonusPointsEnum = Arrays.stream(BonusPointsEnum.values())
                    .filter(item -> item.bonusItem().equals(bonusItem))
                    .findFirst()
                    .orElseThrow(() -> new ClientException("无效的加分项:" + bonusItem));

            // 依次找到对应的明细记录 总积分记录
            LambdaQueryWrapper<UserDetailBonusDo> detailQueryWrapper = Wrappers.lambdaQuery(UserDetailBonusDo.class)
                    .eq(UserDetailBonusDo::getUserId, requestParam.getUserId())
                    .eq(UserDetailBonusDo::getBonusItem, bonusItem)
                    .eq(UserDetailBonusDo::getDel_flag, 0);
            LambdaQueryWrapper<UserBonusDo> bonusQueryWrapper = Wrappers.lambdaQuery(UserBonusDo.class)
                    .eq(UserBonusDo::getUserId, requestParam.getUserId())
                    .eq(UserBonusDo::getDel_flag, 0);

            // 使用 selectList 查询多个记录
            List<UserDetailBonusDo> userDetailBonusList = baseMapper.selectList(detailQueryWrapper);
            UserBonusDo userBonusDo = userBonusMapper.selectOne(bonusQueryWrapper);

            // 非空检查，防止空指针异常
            if (userDetailBonusList == null) {
                throw new ClientException("未找到对应的加分明细记录");
            }
            if (userBonusDo == null) {
                throw new ClientException("未找到对应的总积分记录");
            }

            UserDetailBonusDo userDetailBonusDo = userDetailBonusList.get(0);

            // 逻辑删除对应的明细记录
            userDetailBonusDo.setDel_flag(1);
            LambdaUpdateWrapper<UserDetailBonusDo> bonusDoLambdaUpdateWrapper = Wrappers.lambdaUpdate(UserDetailBonusDo.class)
                    .eq(UserDetailBonusDo::getIntegrationId, userDetailBonusDo.getIntegrationId()); // 使用主键进行更新
            int detailUpdate = baseMapper.update(userDetailBonusDo, bonusDoLambdaUpdateWrapper);
            if (detailUpdate != 1) {
                throw new DatabaseException("删除加分明细记录失败");
            }

            // 扣减总积分记录
            String bonusType = userDetailBonusDo.getBonusType();
            int points = userDetailBonusDo.getPoints();
            int totalBonus = userBonusDo.getTotalBonus();

            // 根据加分类型更新积分
            if (bonusType.equals("日常社团活动")) {
                int dailyBonus = userBonusDo.getDailyBonus() - points;
                userBonusDo.setDailyBonus(dailyBonus);
            } else if (bonusType.equals("项目活动策划")) {
                int projectBonus = userBonusDo.getProjectBonus() - points;
                userBonusDo.setProjectBonus(projectBonus);
            }
            userBonusDo.setTotalBonus(totalBonus - points);

            LambdaUpdateWrapper<UserBonusDo> userBonusDoLambdaUpdateWrapper = Wrappers.lambdaUpdate(UserBonusDo.class)
                    .eq(UserBonusDo::getUserId, requestParam.getUserId())
                    .eq(UserBonusDo::getDel_flag, 0);
            int bonusUpdate = userBonusMapper.update(userBonusDo, userBonusDoLambdaUpdateWrapper);
            if (bonusUpdate != 1) {
                throw new DatabaseException("更新用户总积分失败");
            }
        } catch (ClientException | DatabaseException e) {
            // 捕获自定义异常，直接抛出
            throw e;
        } catch (TransactionException e) {
            // 捕获事务异常并抛出
            throw new ServiceException("删除积分操作中发生事务错误: " + e.getMessage());
        } catch (Exception e) {
            // 捕获其他未知异常
            throw new ServiceException("删除用户加分记录时发生未知错误: " + e.getMessage());
        }
    }

    /**
     * 删除总积分
     * @param requestParam
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteUserBonus(DeleteBonusReqDTO requestParam) {
        try {
            // 构建删除用户加分明细的更新条件
            LambdaUpdateWrapper<UserDetailBonusDo> userDetailBonusDoLambdaUpdateWrapper = Wrappers.lambdaUpdate(UserDetailBonusDo.class)
                    .eq(UserDetailBonusDo::getUserId, requestParam.getUserId())
                    .eq(UserDetailBonusDo::getDel_flag, 0)
                    .set(UserDetailBonusDo::getDel_flag, 1);

            // 构建删除用户总积分的更新条件
            LambdaUpdateWrapper<UserBonusDo> userBonusDoLambdaUpdateWrapper = Wrappers.lambdaUpdate(UserBonusDo.class)
                    .eq(UserBonusDo::getUserId, requestParam.getUserId())
                    .eq(UserBonusDo::getDel_flag, 0)
                    .set(UserBonusDo::getDel_flag, 1);

            // 执行更新操作
            int detailUpdate = baseMapper.update(null, userDetailBonusDoLambdaUpdateWrapper);
            int bonusUpdate = userBonusMapper.update(null, userBonusDoLambdaUpdateWrapper);

            // 检查更新结果
            if (detailUpdate <= 0) {
                throw new DatabaseException("删除用户加分明细记录失败");
            }
            if (bonusUpdate <= 0) {
                throw new DatabaseException("删除用户总积分记录失败");
            }
        } catch (DatabaseException e) {
            // 捕获数据库相关异常并抛出
            throw e;
        } catch (TransactionException e) {
            // 捕获事务异常并抛出
            throw new ServiceException("删除操作中发生事务错误: " + e.getMessage());
        } catch (Exception e) {
            // 捕获其他异常
            throw new ServiceException("删除用户总积分时发生未知错误: " + e.getMessage());
        }
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
                existDo.setTotalBonus(newTotalBonus);
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
