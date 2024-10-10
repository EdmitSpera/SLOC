package com.learning.springboot.admin.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learning.springboot.admin.dao.entity.ExcelReadUserDo;
import com.learning.springboot.admin.dao.entity.ExcelWriteUserDo;
import com.learning.springboot.admin.dao.entity.UserDo;
import com.learning.springboot.admin.dao.mapper.UserMapper;
import com.learning.springboot.admin.dto.req.*;
import com.learning.springboot.admin.dto.resp.UserAccountRespDTO;
import com.learning.springboot.admin.dto.resp.UserInformationRespDTO;
import com.learning.springboot.admin.service.UserService;
import com.learning.springboot.admin.util.DigitNumberGenerator;
import com.learning.springboot.framework.exception.ClientException;
import com.learning.springboot.framework.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.learning.springboot.admin.common.constants.RedisCacheConstant.ADMIN_USER_KEY;
import static com.learning.springboot.framework.errorcode.BaseErrorCode.USER_DONT_EXIT;
import static com.learning.springboot.framework.errorcode.BaseErrorCode.USER_NAME_VERIFY_ERROR;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDo> implements UserService {

    private final DigitNumberGenerator generator = new DigitNumberGenerator();

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 新增用户
     *
     * @param requestParam
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(registerUserReqDTO requestParam) {
        try {
            UserDo userDo = BeanUtil.toBean(requestParam, UserDo.class);
            baseMapper.insert(userDo);

            // 加入缓存
            String username = requestParam.getUsername();
            String userKey = ADMIN_USER_KEY + username;

            stringRedisTemplate.opsForHash().put(
                    userKey,
                    username,
                    JSON.toJSONString(userDo)
            );
            stringRedisTemplate.expire(userKey, 30, TimeUnit.DAYS); // 30天过期
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("sloc_username__uindex")) {
                throw new ClientException("注册失败：用户名 " + requestParam.getUsername() + " 已存在，请使用其他用户名");
            } else {
                throw new ServiceException("注册失败：" + e.getMessage());
            }
        } catch (Exception e) {
            throw new ServiceException("注册过程中出现未知错误：" + e.getMessage());
        }
    }


    /**
     * 表格导入数据 新增用户
     *
     * @param filePath
     */
    @Override
    @Transactional(rollbackFor = Exception.class) // 开启事务管理，遇到异常自动回滚
    public void registerUsersExcel(String filePath) {
        try {
            EasyExcel.read(filePath, ExcelReadUserDo.class, new PageReadListener<ExcelReadUserDo>(dataList -> {
                for (ExcelReadUserDo excelReadUserDo : dataList) {
                    try {
                        register(BeanUtil.toBean(buildUserDoBasedOnExcel(excelReadUserDo), registerUserReqDTO.class));
                    } catch (DataIntegrityViolationException e) {
                        // 捕获数据库违反唯一约束异常
                        if (e.getMessage().contains("sloc_username__uindex")) {
                            throw new ClientException("导入失败：用户名 " + excelReadUserDo.getRealName() + " 账号已存在，请检查Excel文件");
                        } else {
                            throw new ServiceException("导入失败：" + e.getMessage());
                        }
                    }
                }
            })).sheet().doRead();
        } catch (Exception e) {
            throw new ServiceException("Excel导入过程中出现错误，操作已回滚：" + e.getMessage());
        }
    }

    /**
     * 用户账号解析和密码生成
     *
     * @param excelReadUserDo 本地路径
     * @return
     */
    public UserDo buildUserDoBasedOnExcel(ExcelReadUserDo excelReadUserDo) {
        String[] splitTemp = excelReadUserDo.getMail().split("@");
        String userName = splitTemp[0];
        String password = "xp" + generator.generateSixDigitNumber();
        UserDo resultBean = BeanUtil.toBean(excelReadUserDo, UserDo.class);
        resultBean.setUsername(userName);
        resultBean.setPassword(password);
        return resultBean;
    }

    /**
     * 按照年级输出excel文档
     *
     * @param grade    按照年级分组
     * @param filePath 文件输出路径
     */
    public void writeExcelBasedDatabase(int grade, String filePath) {
        LambdaQueryWrapper<UserDo> queryWrapper = Wrappers.lambdaQuery(UserDo.class)
                .eq(UserDo::getGrade, grade)
                .eq(UserDo::getDelFlag, 0);
        Collection<UserDo> userDoList = baseMapper.selectList(queryWrapper);

        // 将 userDoList 转换为 ExcelWriteUserDo 对象的集合
        Collection<ExcelWriteUserDo> excelWriteUserDoList = userDoList.stream().map(userDo -> {
            ExcelWriteUserDo excelUser = new ExcelWriteUserDo();
            excelUser.setStudentNumber(userDo.getStudentNumber());
            excelUser.setRealName(userDo.getRealName());
            excelUser.setNickname(userDo.getNickname());
            excelUser.setGender(userDo.getGender());
            excelUser.setDepartment(userDo.getDepartment());
            excelUser.setGrade(userDo.getGrade());
            excelUser.setMajor(userDo.getMajor());
            excelUser.setMail(userDo.getMail());
            excelUser.setPhone(userDo.getPhone());
            return excelUser;
        }).collect(Collectors.toList());
        String fileName = filePath + "\\" + grade + "member.xlsx";
        EasyExcel.write(fileName, ExcelWriteUserDo.class)
                .sheet("write")
                .doWrite(() -> {
                    return excelWriteUserDoList;
                });
    }

    /**
     * 根据用户名删除用户
     *
     * @param requestParam
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(deleteUserReqDTO requestParam) {
        LambdaQueryWrapper<UserDo> queryWrapper = Wrappers.lambdaQuery(UserDo.class)
                .eq(UserDo::getDelFlag, 0)
                .eq(UserDo::getUsername, requestParam.getUsername());
        UserDo userDo = baseMapper.selectOne(queryWrapper);

        if (userDo == null) {
            throw new ClientException(USER_NAME_VERIFY_ERROR);
        }

        LambdaUpdateWrapper<UserDo> updateWrapper = Wrappers.lambdaUpdate(userDo)
                .eq(UserDo::getUsername, requestParam.getUsername())
                .set(UserDo::getDelFlag, 1);
        int updateRow = baseMapper.update(null, updateWrapper);

        if (updateRow == 0) {
            throw new ServiceException("删除用户失败");
        }

        // 删除缓存中的用户
        String username = requestParam.getUsername();
        String userKey = ADMIN_USER_KEY + username;
        stringRedisTemplate.delete(userKey); // 删除缓存
    }


    /**
     * 根据学号获取账号信息
     *
     * @param requestParam 学号
     * @return
     */
    @Override
    public UserAccountRespDTO getUserAccount(UserAccountReqDTO requestParam) {
        LambdaQueryWrapper<UserDo> queryWrapper = Wrappers.lambdaQuery(UserDo.class)
                .eq(UserDo::getDelFlag, 0)
                .eq(UserDo::getStudentNumber, requestParam.getStudentNumber());
        UserDo userDo = baseMapper.selectOne(queryWrapper);

        if (userDo == null) {
            throw new ClientException(USER_DONT_EXIT);
        }

        return BeanUtil.toBean(userDo, UserAccountRespDTO.class);
    }


    /**
     * 根据真实姓名获取用户信息
     *
     * @param requestParam 真名
     * @return
     */
    @Override
    public UserInformationRespDTO getUserInformation(UserInformationReqDTO requestParam) {
        LambdaQueryWrapper<UserDo> queryWrapper = Wrappers.lambdaQuery(UserDo.class)
                .eq(UserDo::getDelFlag, 0)
                .eq(UserDo::getRealName, requestParam.getRealName());
        UserDo userDo = baseMapper.selectOne(queryWrapper);

        if (userDo == null) {
            throw new ClientException(USER_DONT_EXIT);
        }

        return BeanUtil.toBean(userDo, UserInformationRespDTO.class);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserAccount(updateUserAccReqDTO requestParam) {
        // 查询当前账号
        LambdaQueryWrapper<UserDo> queryWrapper = Wrappers.lambdaQuery(UserDo.class)
                .eq(UserDo::getDelFlag, 0)
                .eq(UserDo::getUsername, requestParam.getUsername());
        UserDo userDo = baseMapper.selectOne(queryWrapper);

        if (userDo == null) {
            throw new ClientException(USER_DONT_EXIT);
        }

        // 判断密码是否一致
        if (Objects.equals(userDo.getPassword(), requestParam.getPassword())) {
            throw new ClientException("新密码不能与原密码一致");
        }

        // 不一致则进行修改
        LambdaUpdateWrapper<UserDo> updateWrapper = Wrappers.lambdaUpdate(userDo)
                .eq(UserDo::getUsername, requestParam.getUsername())
                .set(UserDo::getPassword, requestParam.getPassword());
        int updateRow = baseMapper.update(null, updateWrapper);

        if (updateRow == 0) {
            throw new ServiceException("修改账号失败，请稍后重试");
        }

        // 修改成功后更新缓存
        try {
            String username = requestParam.getUsername();
            String userKey = ADMIN_USER_KEY + username;

            // 删除旧缓存数据
            stringRedisTemplate.delete(userKey);

            // 插入新的用户信息到缓存
            userDo.setPassword(requestParam.getPassword()); // 更新用户信息中的密码
            stringRedisTemplate.opsForHash().put(
                    userKey,
                    username,
                    JSON.toJSONString(userDo)
            );
            stringRedisTemplate.expire(userKey, 30, TimeUnit.DAYS); // 30天过期

        } catch (Exception e) {
            throw new ServiceException("更新缓存时发生错误：" + e.getMessage());
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserInformation(updateUserInfoReqDTO requestParam) {
        // 查询当前账号
        LambdaQueryWrapper<UserDo> queryWrapper = Wrappers.lambdaQuery(UserDo.class)
                .eq(UserDo::getDelFlag, 0)
                .eq(UserDo::getUsername, requestParam.getUsername());
        UserDo userDo = baseMapper.selectOne(queryWrapper);

        if (userDo == null) {
            throw new ClientException(USER_DONT_EXIT);
        }

        // 更新数据库中的用户信息
        LambdaUpdateWrapper<UserDo> updateWrapper = Wrappers.lambdaUpdate(userDo)
                .eq(UserDo::getDelFlag, 0)
                .eq(UserDo::getUsername, requestParam.getUsername());
        int updateRow = baseMapper.update(BeanUtil.toBean(requestParam, UserDo.class), updateWrapper);

        if (updateRow == 0) {
            throw new ServiceException("修改用户信息失败，请稍后重试");
        }

        // 修改成功后更新缓存
        try {
            String username = requestParam.getUsername();
            String userKey = ADMIN_USER_KEY + username;

            // 删除旧缓存数据
            stringRedisTemplate.delete(userKey);

            // 插入新的用户信息到缓存
            stringRedisTemplate.opsForHash().put(
                    userKey,
                    username,
                    JSON.toJSONString(userDo)
            );
            stringRedisTemplate.expire(userKey, 30, TimeUnit.DAYS); // 30天过期

        } catch (Exception e) {
            throw new ServiceException("更新缓存时发生错误：" + e.getMessage());
        }
    }

}
