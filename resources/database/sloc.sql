create database sloc;
use sloc;

create table sloc_user
(
    user_id         bigint auto_increment comment '用户ID'
        primary key,
    student_number  varchar(256)                      null comment '学号',
    username        varchar(256)                      null comment '用户名',
    password        varchar(512)                      null comment '密码',
    real_name       varchar(256)                      null comment '真实姓名',
    nickname        varchar(512)                      null comment '户外ID',
    gender          enum ('男', '女')                 not null comment '性别',
    department      varchar(512)                      not null comment '部门',
    grade           tinyint                           null comment '年级',
    major           varchar(512)                      null comment '专业',
    mail            varchar(512)                      null comment '校内邮箱',
    phone           varchar(128)                      null comment '电话',
    campus          enum ('桑浦山校区', '东海岸校区') null comment '校区',
    dormitory       varchar(256)                      null comment '宿舍',
    birthday        datetime                          null comment '生日',
    nation          varchar(10)                       null comment '民族',
    politics_status varchar(256)                      null comment '政治面貌',
    create_time     datetime                          null comment '注册时间',
    modify_time     datetime                          null comment '修改日期',
    deletion_time   datetime                          null comment '注销时间',
    del_flag        tinyint                           null comment '逻辑删除标识',
    admin_flag      tinyint                           null comment '管理员标识',
    constraint sloc_username__uindex
        unique (username)
)
    comment '协会成员用户表';
create table sloc_user_bonus
(
    summary_id    bigint  not null comment '总积分id'
        primary key,
    user_id       bigint  null comment '用户id',
    daily_bonus   int     null comment '日常活动积分',
    project_bonus int     null comment '项目活动积分',
    total_bonus   int     null comment '总积分',
    del_flag      tinyint null
);

create table sloc_user_detail_bonus
(
    integration_id      bigint       not null comment '积分明细id'
        primary key,
    user_id             bigint       null comment '用户id',
    bonus_type          varchar(512) null comment '加分类型',
    bonus_item          varchar(512) null comment '加分明细',
    points              tinyint      null comment '分值',
    semester_max_points tinyint      null comment '学期封顶',
    academic            varchar(256) null comment '学期信息',
    create_time         datetime     null,
    del_flag            tinyint      null
);

create table sloc_equipment
(
    equipment_id bigint                                                        not null comment '装备id'
        primary key,
    name         varchar(512)                                                  null comment '装备名称',
    type         enum ('户外鞋服', '露营装备', '电子设备', '攀登装备', '其他') null comment '类型',
    sum          int                                                           null comment '数量',
    rent         decimal                                                       null comment '单件租金',
    area         varchar(256)                                                  null comment '区',
    col          varchar(256)                                                  null comment '列',
    tier         varchar(256)                                                  null comment '层',
    del_flag     tinyint                                                       null
);

create table sloc_project
(
    project_id   bigint auto_increment comment '主键'
        primary key,
    project_name varchar(512)                                                                                                    null comment '项目名称',
    type         enum ('户外徒步', '户外露营拓展', '暑期大项目', '文体活动', '协会团队建设', '知识培训', '志愿活动', '体能训练') null comment '项目类型',
    status       enum ('策划阶段', '实施阶段', '完成阶段')                                                                       null comment '项目状态',
    begin_time   datetime                                                                                                        null comment '项目开展时间',
    end_time     datetime                                                                                                        null comment '项目结束时间',
    del_flag     tinyint                                                                                                         null comment '逻辑删除标识'
)
    comment '项目表';

create table sloc_project_member
(
    member_id  bigint auto_increment comment '主键'
        primary key,
    user_id    bigint                                                                                  null comment '用户Id',
    project_id bigint                                                                                  null comment '项目id',
    role_type  enum ('组长', '副组长', '装备委员', '宣传委员', '后勤委员', '文秘委员', '项目跟进人员') null comment '项目角色',
    del_flag   tinyint                                                                                 null comment '逻辑删除标识'
);

