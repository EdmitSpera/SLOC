package com.learning.springboot.admin.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.learning.springboot.admin.dao.entity.UserDo;
import org.springframework.stereotype.Component;

@Component
public interface UserMapper extends BaseMapper<UserDo> {
}
