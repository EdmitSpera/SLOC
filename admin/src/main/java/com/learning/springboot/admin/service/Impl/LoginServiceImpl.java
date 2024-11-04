package com.learning.springboot.admin.service.Impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learning.springboot.admin.dao.entity.UserDo;
import com.learning.springboot.admin.dao.mapper.UserMapper;
import com.learning.springboot.admin.dto.admin.req.LoginReqDTO;
import com.learning.springboot.admin.dto.admin.resp.LoginRespDTO;
import com.learning.springboot.admin.service.LoginService;
import com.learning.springboot.framework.exception.ClientException;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.util.Date;

import static com.learning.springboot.framework.constants.JwtConstant.JWT_KEY;
import static com.learning.springboot.framework.constants.RoleConstant.*;

@Service
public class LoginServiceImpl extends ServiceImpl<UserMapper, UserDo>implements LoginService {

    @Override
    public LoginRespDTO login(LoginReqDTO requestParam, ServletResponse response) {
        LambdaQueryWrapper<UserDo> queryWrapper = Wrappers.lambdaQuery(UserDo.class)
                .eq(UserDo::getUsername, requestParam.getUsername())
                .eq(UserDo::getPassword, requestParam.getPassword())
                .eq(UserDo::getDelFlag, 0);

        UserDo userDo = baseMapper.selectOne(queryWrapper);
        if(userDo==null){
            throw new ClientException("用户名或密码错误");
        }else if(userDo.getAdminFlag() == 0){
            ((HttpServletResponse)response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            throw new ClientException("用户权限不足");
        }

        // 权限判断
        String role = switch (userDo.getAdminFlag()) {
            case 1 -> EQUIPMENT.roleType();
            case 2 -> SECRETARY.roleType();
            case 3 -> MANAGEMENT.roleType();
            case 4 -> SUPER_ADMIN.roleType();
            default -> "普通用户";
        };
        String token = JWT.create()
                .withIssuer("sloc")
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 60 * 60 * 1000))
                .withClaim("role", role)
                .withClaim("username", requestParam.getUsername())
                .sign(Algorithm.HMAC256(JWT_KEY));

        LoginRespDTO loginRespDTO = new LoginRespDTO();
        loginRespDTO.setToken(token);
        return loginRespDTO;
    }
}
