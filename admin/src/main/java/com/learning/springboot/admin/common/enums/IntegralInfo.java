package com.learning.springboot.admin.common.enums;

public interface IntegralInfo {
    /**
     * 加分类型
     * @return
     */
    String bonusType();

    /**
     * 加分项
     * @return
     */
    String bonusItem();

    /**
     * 加分数值
     * @return
     */
    Integer points();

    /**
     * 学期分数封顶
     * @return
     */
    Integer semesterMaxPoints();
}
