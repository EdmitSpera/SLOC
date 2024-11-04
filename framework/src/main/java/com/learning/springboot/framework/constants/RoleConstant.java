package com.learning.springboot.framework.constants;

public enum RoleConstant implements IntegralRole {

    EQUIPMENT("装备部", 1),
    SECRETARY("秘书部", 2),
    MANAGEMENT("管理层", 3),
    SUPER_ADMIN("超级管理员", 4);

    private final String roleType;
    private final Integer roleFlag;

    RoleConstant(String roleType, Integer roleFlag) {
        this.roleType = roleType;
        this.roleFlag = roleFlag;
    }

    @Override
    public String roleType() {
        return roleType;
    }

    @Override
    public Integer roleFlag() {
        return roleFlag;
    }
}
