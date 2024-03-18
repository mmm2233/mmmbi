# 数据库初始化
-- 创建库
create database if not exists my_db;
-- 切换库
use my_db;

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
    status varchar(128) not null default 'wait'  comment 'wait,running,succeed,failed',
    execMessage text null comment '执行信息',
    userId  bigint      default 0                 not null comment '创建用户id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除'
) comment '图表' collate = utf8mb4_unicode_ci;

-- 充值订单表
create table if not exists orders
(
    id            bigint auto_increment comment 'id' primary key comment '订单id',
    alipayTradeNo varchar(128)                       null comment '支付宝交易凭证id',
    `userId`      bigint                             NOT NULL COMMENT '用户id',
    subject       varchar(128)                       not null comment '交易名称',
    totalAmount   double                             not null comment '交易金额',
    tradeStatus   varchar(128)                       not null default 'unpaid ' comment 'unpaid,paying,succeed,failed',
    buyerId       varchar(64)                        null comment '支付宝买家id',
    createTime    datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime    datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete      tinyint  default 0                 not null comment '是否删除'
) comment '充值订单表' collate = utf8mb4_unicode_ci;

-- 文本任务表
create table if not exists text_task
(
    id             bigint auto_increment comment '任务id' primary key,
    `name`         varchar(128)                       null comment '笔记名称',
    textType       varchar(128)                       null comment '文本类型',
    genTextContent text                               null comment '生成的文本内容',
    userId         bigint                             null comment '创建用户Id',
    `status`       varchar(128)                       not null default 'wait' comment 'wait,running,succeed,failed',
    execMessage    text                               null comment '执行信息',
    createTime     datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime     datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete       tinyint  default 0                 not null comment '是否删除'
) comment '文本任务表' collate = utf8mb4_unicode_ci;

-- 文本记录表
create table if not exists text_record
(
    id             bigint auto_increment comment 'id' primary key,
    textTaskId     bigint comment '文本任务id',
    textContent    text                               null comment '文本内容',
    genTextContent text                               null comment '生成的文本内容',
    `status`       varchar(128)                       not null default 'wait' comment 'wait,running,succeed,failed',
    execMessage    text                               null comment '执行信息',
    createTime     datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime     datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete       tinyint  default 0                 not null comment '是否删除'
) comment '文本记录表' collate = utf8mb4_unicode_ci;

-- 图片分析表
create table if not exists image
(
    id         bigint auto_increment comment 'id' primary key,
    goal       text null comment '分析目标',
    imageType  varchar(512) null comment '图片类型',
    baseString varchar(2048) null comment  '图表base64编码',
    state varchar(256) null comment '图片分析状态',
    execMessage varchar(256) null  comment '执行信息',
    genResult  text null comment  '生成的分析结论',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除'
) comment '图片分析表' collate = utf8mb4_unicode_ci;


-- 分析图表表
create table if not exists chart_logs
(
    id         bigint auto_increment comment 'id' primary key,
    chartId bigint not null comment '图表id',
    genResult   text                     null comment '生成分析结果',
    userId  bigint      default 0                 not null comment '创建用户id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    isDelete   tinyint  default 0                 not null comment '是否删除'
) comment '图表' collate = utf8mb4_unicode_ci;


create table if not exists points
(
    id         bigint auto_increment comment 'id' primary key,
    userId     bigint                             null comment '创建用户Id',
    remainingPoints bigint default 0 not null comment '剩余积分数量',
    totalPoints bigint default 0 not null comment '总积分数量',
    creditTotal bigint null  comment '总积分' default 0,
    status tinyint default 1 not null comment '积分状态，有效1无效0',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除'
) comment '积分表' collate = utf8mb4_unicode_ci;

create table if not exists point_changes
(
    id         bigint auto_increment comment 'id' primary key,
    userId     bigint                             null comment '创建用户Id',
    changeAmount bigint not null comment '积分变动值',
    changeType bigint not null comment '积分变动类型',
    reason varchar(256) null  comment '积分变动原因',
    newPoints bigint  comment '积分变动后数量',
    source varchar(256) null  comment '积分来源',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    isDelete   tinyint  default 0                 not null comment '是否删除'
) comment '积分表' collate = utf8mb4_unicode_ci;

