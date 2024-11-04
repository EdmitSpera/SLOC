package com.learning.springboot.admin.common.enums;

public enum BonusPointsEnum implements IntegralInfo {
    // 日常社团活动加分
    RUNNING("日常社团活动", "长跑1次", 1, 10),
    EQUIPMENT_WORK("日常社团活动", "参与装备库工作", 2, 10),
    TWEET_WORK("日常社团活动", "推文制作", 2, 10),
    SKILL_TRAINING("日常社团活动", "参加技能培训1次", 2, null),
    FITNESS_TRAINING("日常社团活动", "参加体能培训1次", 2, null),
    CLIMBING("日常社团活动", "参与攀岩房攀岩活动1次", 1, 5),
    NIGHT_RUNNING("日常社团活动", "带夜跑1次", 2, null),
    TRAINING_LEADER("日常社团活动", "担任技能培训讲师或体能培训领队1次", 4, null),

    // 项目活动策划加分
    CAMP_LEADER("项目活动策划", "担任外出露营活动组长/项目负责人1次", 15, null),
    CAMP_ASSISTANT_LEADER("项目活动策划", "担任外出露营活动副组长、文秘等项目组成员1次", 10, null),
    CAMP_HELPER("项目活动策划", "担任外出露营活动助理1次", 5, null),
    INDOOR_EVENT_LEADER("项目活动策划", "担任校内活动组长/负责人1次", 10, null),
    INDOOR_EVENT_ASSISTANT_LEADER("项目活动策划", "担任校内活动副组长、文秘等项目组成员1次", 5, null),
    INDOOR_EVENT_HELPER("项目活动策划", "参加校内活动或担任活动志愿者1次", 2, null),
    PROJECT_PLAN_TEAM("项目活动策划", "制作项目/活动计划书之团队1次", 20, null);

    private final String bonusType;
    private final String bonusItem;
    private final Integer points;
    private final Integer semesterMaxPoints;

    BonusPointsEnum(String bonusType, String bonusItem, Integer points, Integer semesterMaxPoints) {
        this.bonusType = bonusType;
        this.bonusItem = bonusItem;
        this.points = points;
        this.semesterMaxPoints = semesterMaxPoints;
    }

    @Override
    public String bonusType() {
        return bonusType;
    }

    @Override
    public String bonusItem() {
        return bonusItem;
    }

    @Override
    public Integer points() {
        return points;
    }

    @Override
    public Integer semesterMaxPoints() {
        return semesterMaxPoints;
    }
}
