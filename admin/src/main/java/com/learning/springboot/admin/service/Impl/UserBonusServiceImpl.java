package com.learning.springboot.admin.service.Impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learning.springboot.admin.common.enums.BonusPointsEnum;
import com.learning.springboot.admin.dao.entity.UserBonusDo;
import com.learning.springboot.admin.dao.entity.UserDetailBonusDo;
import com.learning.springboot.admin.dao.entity.UserDo;
import com.learning.springboot.admin.dao.mapper.UserBonusMapper;
import com.learning.springboot.admin.dao.mapper.UserDetailBonusMapper;
import com.learning.springboot.admin.dao.mapper.UserMapper;
import com.learning.springboot.admin.dto.interation.req.DeleteBonusReqDTO;
import com.learning.springboot.admin.dto.interation.req.DeleteDetailBonusReqDTO;
import com.learning.springboot.admin.dto.interation.req.UserDetailBonusReqDTO;
import com.learning.springboot.admin.dto.interation.resp.BonusItemRespDTO;
import com.learning.springboot.admin.dto.interation.resp.GetBonusRespDTO;
import com.learning.springboot.admin.dto.interation.resp.GetDetailBonusRespDTO;
import com.learning.springboot.admin.service.UserBonusService;
import com.learning.springboot.admin.util.SemesterUtil;
import com.learning.springboot.framework.dto.CustomPageRespDTO;
import com.learning.springboot.framework.exception.ClientException;
import com.learning.springboot.framework.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import net.sf.jsqlparser.util.validation.metadata.DatabaseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserBonusServiceImpl extends ServiceImpl<UserDetailBonusMapper, UserDetailBonusDo> implements UserBonusService {

    private final SemesterUtil semesterUtil = new SemesterUtil();

    private final UserBonusMapper userBonusMapper;

    private final UserMapper userMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addUserDetailBonus(UserDetailBonusReqDTO requestParam) {

        BonusPointsEnum bonusPointsEnum = BonusPointsEnum.valueOf(requestParam.getBonusItem());

        LambdaQueryWrapper<UserDo> lambdaQueryWrapper = Wrappers.lambdaQuery(UserDo.class)
                .eq(UserDo::getDelFlag, 0)
                .eq(UserDo::getRealName, requestParam.getRealName());

        UserDo userDo = userMapper.selectOne(lambdaQueryWrapper);
        if (userDo == null) {
            throw new ClientException("未找到对应的用户信息");
        }

        // 判断当前学年段
        String currentSemester = SemesterUtil.getCurrentSemester();

        Integer semesterMaxPoints = Optional.ofNullable(bonusPointsEnum.semesterMaxPoints()).orElse(0);

        // 如果查询出来的项目类型是学期限制分数 查询当前学年度中是否超额
        if (semesterMaxPoints != 0) {
            LambdaQueryWrapper<UserDetailBonusDo> queryWrapper = Wrappers.lambdaQuery(UserDetailBonusDo.class)
                    .eq(UserDetailBonusDo::getUserId, userDo.getUserId())
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
                .userId(Long.valueOf(userDo.getUserId()))
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
     *
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
     *
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


    @Override
    @Transactional(readOnly = true)
    public CustomPageRespDTO<GetDetailBonusRespDTO> getDetailBonus(String page, String perPage, String userId, String bonusType, String academic) {
        try {
            // 构造分页
            Long curPage = Long.valueOf(page);
            Long curPerPage = Long.valueOf(perPage);
            if(userId.isEmpty()){   // TODO 硬编码 后期修复初始不传user_id的bug
                return new CustomPageRespDTO<>(null,0,0,0,0);
            }

            Long curUserId = Long.valueOf(userId);
            Page<UserDetailBonusDo> newPage = new Page<>(curPage, curPerPage);


            // 查询条件
            LambdaQueryWrapper<UserDetailBonusDo> queryWrapper = Wrappers.lambdaQuery(UserDetailBonusDo.class)
                    .eq(UserDetailBonusDo::getUserId, curUserId)
                    .eq(UserDetailBonusDo::getDel_flag, 0);

            // 查询基础用户信息
            LambdaQueryWrapper<UserDo> userQueryWrapper = Wrappers.lambdaQuery(UserDo.class)
                    .eq(UserDo::getUserId, curUserId)
                    .eq(UserDo::getDelFlag, 0);

            // 查询用户和积分信息
            UserDo userDo = userMapper.selectOne(userQueryWrapper);
            if (userDo == null) {
                throw new ClientException("未找到对应的用户积分信息");
            }

            if(!Objects.equals(bonusType, "")){
                queryWrapper.eq(UserDetailBonusDo::getBonusType, bonusType);
            }

            if(!Objects.equals(academic, "")){
                queryWrapper.eq(UserDetailBonusDo::getAcademic, academic);
            }

            // 分页查询
            IPage<UserDetailBonusDo> detailBonusDoPage = baseMapper.selectPage(newPage, queryWrapper);
            if (detailBonusDoPage == null || detailBonusDoPage.getRecords().isEmpty()) {
                throw new ClientException("未找到用户的加分明细记录");
            }

            IPage<GetDetailBonusRespDTO> pageResult = detailBonusDoPage.convert(each -> {
                try {
                    return BeanUtil.toBean(each, GetDetailBonusRespDTO.class);
                } catch (Exception e) {
                    throw new ServiceException("转换加分明细记录失败: " + e.getMessage());
                }
            });
            // 转换为响应DTO
            return new CustomPageRespDTO<>(
                    pageResult.getRecords(),
                    pageResult.getTotal(),
                    pageResult.getSize(),
                    pageResult.getCurrent(),
                    pageResult.getPages()
            );

        } catch (ClientException e) {
            // 捕获自定义异常
            throw e;
        } catch (Exception e) {
            // 捕获其他未知异常
            throw new ServiceException("查询用户加分明细时发生未知错误: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public CustomPageRespDTO<GetBonusRespDTO> getBonus(String page, String perPage, String grade, String department, String realName) {
        try {
            // 构造分页
            long curPage = Long.parseLong(page);
            long curPerPage = Long.parseLong(perPage);
            Page<UserBonusDo> newPage = new Page<>(curPage, curPerPage);

            // 查询基础用户信息
            LambdaQueryWrapper<UserDo> userQueryWrapper = Wrappers.lambdaQuery(UserDo.class)
                    .eq(UserDo::getDelFlag, 0); // 过滤未删除的用户

            if (!Objects.equals(grade, "")) {
                userQueryWrapper.eq(UserDo::getGrade, grade); // 根据年级过滤
            }

            if (!Objects.equals(department, "")) {
                userQueryWrapper.eq(UserDo::getDepartment, department); // 根据部门过滤
            }

            if (!Objects.equals(realName, "")) {
                userQueryWrapper.eq(UserDo::getRealName, realName); // 根据部门过滤
            }

            // 查询用户信息
            List<UserDo> userDos = userMapper.selectList(userQueryWrapper);
            if (userDos == null || userDos.isEmpty()) {
                throw new ClientException("未找到对应的用户信息");
            }

            // 获取用户ID列表
            List<Long> userIds = userDos.stream().map(UserDo::getUserId).toList();

            // 查询积分信息
            LambdaQueryWrapper<UserBonusDo> queryWrapper = Wrappers.lambdaQuery(UserBonusDo.class)
                    .in(UserBonusDo::getUserId, userIds)  // 只查询指定用户ID的积分
                    .eq(UserBonusDo::getDel_flag, 0); // 过滤未删除的积分记录

            // 分页查询积分信息
            Page<UserBonusDo> userBonusDoPage = userBonusMapper.selectPage(newPage, queryWrapper);
            if (userBonusDoPage == null || userBonusDoPage.getRecords().isEmpty()) {
                throw new ClientException("未找到用户的加分明细记录");
            }

            // 创建返回对象列表，合并用户信息和积分信息
            List<GetBonusRespDTO> bonusRespDTOs = new ArrayList<>();
            for (UserDo userDo : userDos) {
                // 获取当前用户的积分记录
                UserBonusDo bonusDo = userBonusDoPage.getRecords().stream()
                        .filter(bonus -> bonus.getUserId().equals(userDo.getUserId()))
                        .findFirst()
                        .orElse(null); // 如果未找到，返回null

                if (bonusDo != null) {
                    // 合并用户信息和积分信息
                    GetBonusRespDTO bonusRespDTO = GetBonusRespDTO.builder()
                            .userId(String.valueOf(userDo.getUserId()))
                            .grade(userDo.getGrade())
                            .realName(userDo.getRealName())  // 用户真实姓名
                            .department(userDo.getDepartment())  // 用户部门
                            .dailyBonus(bonusDo.getDailyBonus())  // 日常活动积分
                            .projectBonus(bonusDo.getProjectBonus())  // 项目活动积分
                            .totalBonus(bonusDo.getTotalBonus())  // 总积分
                            .build();
                    bonusRespDTOs.add(bonusRespDTO); // 添加到返回列表中
                }
            }

            // 构造分页响应对象

            return new CustomPageRespDTO<>(
                    bonusRespDTOs,  // 数据
                    userBonusDoPage.getTotal(),  // 总条数
                    userBonusDoPage.getSize(),  // 每页大小
                    userBonusDoPage.getCurrent(),  // 当前页
                    userBonusDoPage.getPages()  // 总页数
            );
        } catch (ClientException e) {
            // 捕获自定义异常
            throw e;
        } catch (Exception e) {
            // 捕获其他未知异常
            throw new ServiceException("查询用户总分时发生未知错误: " + e.getMessage());
        }
    }

    @Override
    public List<BonusItemRespDTO> getBonusItemsByType(String bonusType) {
        List<BonusItemRespDTO> result = List.of(BonusPointsEnum.values()).stream()
                .filter(bonus -> bonus.bonusType().equals(bonusType))
                .map(bonus -> new BonusItemRespDTO(bonus.bonusItem(), bonus.name()))
                .collect(Collectors.toList());
        return result;
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
