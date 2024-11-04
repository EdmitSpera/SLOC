package com.learning.springboot.admin.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.learning.springboot.admin.dao.entity.UserDo;
import com.learning.springboot.admin.dto.admin.req.LoginReqDTO;
import com.learning.springboot.admin.dto.admin.resp.LoginRespDTO;
import jakarta.servlet.ServletResponse;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 登录服务
 */
public interface LoginService extends IService<UserDo> {
    LoginRespDTO login(@RequestBody LoginReqDTO requestParam, ServletResponse response);
}
