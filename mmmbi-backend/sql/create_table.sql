# 数据库初始化
-- 创建库
create database if not exists my_bi;

-- 切换库
use my_bi;

-- 用户表
create table if not exists user
(
    id           bigint auto_increment comment 'id' primary key,
    userAccount  varchar(256)                           not null comment '账号',
    userPassword varchar(512)                           not null comment '密码',
    userName     varchar(256)                           null comment '用户昵称',
    userAvatar   varchar(1024)                          null comment '用户头像',
    userRole     varchar(256) default 'user'            not null comment '用户角色：user/admin/ban',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除'
) comment '用户' collate = utf8mb4_unicode_ci;

-- 分析图表表
create table if not exists chart
(
    id         bigint auto_increment comment 'id' primary key,
    goal       text                  not null comment '分析目标',
    chartName      varchar(128) null  comment '图表名称',
    chartData    text                              not null comment '图表数据',
    chartType       varchar(128)                     not null comment '图表类型',
    genResult   text                     null comment '生成分析结果',
    status varchar(128) not null default 'wait'  comment 'wait,running,succeed,failed',
    execMessage text null comment '执行信息',
    userId  bigint      default 0                 not null comment '创建用户id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除'
) comment '图表' collate = utf8mb4_unicode_ci;


create table if not exists credit
(
    id         bigint auto_increment comment 'id' primary key,
    userId     bigint                             null comment '创建用户Id',
    checkTime datetime null comment '签到时间',
    creditTotal bigint null  comment '总积分' default 0,
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除'
) comment '积分表' collate = utf8mb4_unicode_ci;


create table if not exists orders
(
    id         bigint auto_increment comment 'id' primary key comment '订单id',
    alipayTradeNo     varchar(128)              null comment '支付宝交易凭证id',
    subject varchar(128) not null  comment '交易名称' ,
    totalAmount double not null comment '交易金额',
    tradeStatus  varchar(128) not null default 'unpaid ' comment 'unpaid,paying,succeed,failed',
    buyerId varchar(64) null comment '支付宝买家id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除'
) comment '充值订单表' collate = utf8mb4_unicode_ci;
