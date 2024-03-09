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

-- 订单表
create table if not exists orders
(
    id               bigint auto_increment comment 'id' primary key,
    userId           bigint                             not null comment '用户 id',
    purchaseQuantity bigint(0)                          NOT NULL COMMENT '购买数量',
    price            float(255, 2)                      NOT NULL COMMENT '单价',
    totalAmount      float(10, 2)                       NOT NULL COMMENT '交易金额',
    orderStatus      int(0)                             NOT NULL DEFAULT 0 COMMENT '交易状态【0->待付款；1->已完成；2->无效订单,3->删除订单】',
    createTime       datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime       datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete         tinyint  default 0                 not null comment '是否删除'
) comment '订单表' collate = utf8mb4_unicode_ci;

-- 支付记录表
create table if not exists ordersinfo
(
    id              bigint auto_increment comment 'id' primary key,
    userId          bigint                             not null comment '用户 id',
    alipayAccountNo varchar(512)                       not null comment '支付宝流水账号',
    alipayId        varchar(1024) comment '支付宝唯一id',
    orderId         bigint comment '订单id',
    totalAmount     float(10, 2)                       NOT NULL COMMENT '交易金额',
    payStatus       int(0)                             NOT NULL DEFAULT 0 COMMENT '交易状态【0->未支付；1->已完成；2->支付失败】',
    createTime      datetime default CURRENT_TIMESTAMP not null comment '支付时间',
    updateTime      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete        tinyint  default 0                 not null comment '是否删除'
) comment '订单详情表' collate = utf8mb4_unicode_ci;
