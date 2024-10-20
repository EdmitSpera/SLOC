package com.learning.springboot.integration.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;

import java.util.Date;

public class BonusMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        strictInsertFill(metaObject, "delFlag", () -> 0, Integer.class);
        strictInsertFill(metaObject, "createTime", Date::new, Date.class);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
    }
}
