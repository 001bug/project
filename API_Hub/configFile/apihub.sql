/*
 Navicat Premium Data Transfer

 Source Server         : 华为云
 Source Server Type    : MySQL
 Source Server Version : 80100
 Source Host           : 8.130.35.69:3306
 Source Schema         : apihub

 Target Server Type    : MySQL
 Target Server Version : 80100
 File Encoding         : 65001

 Date: 22/11/2023 16:56:02
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for api_order
-- ----------------------------
DROP TABLE IF EXISTS `api_order`;
CREATE TABLE `api_order`
(
    `id`          bigint(0)                                                      NOT NULL AUTO_INCREMENT COMMENT '订单id',
    `totalFee`    int(0)                                                         NOT NULL DEFAULT 0 COMMENT '总金额，单位为分',
    `paymentType` tinyint(3) UNSIGNED ZEROFILL                                   NOT NULL COMMENT '支付类型，1、支付宝，2、微信，3、扣减余额',
    `interfaceId` bigint(0)                                                      NOT NULL COMMENT '接口id',
    `userId`      bigint(0)                                                      NOT NULL COMMENT '用户id',
    `num`         bigint(0)                                                      NOT NULL COMMENT '使用次数',
    `userAddress` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin         NOT NULL COMMENT '用户的id地址',
    `status`      tinyint(0)                                                     NULL     DEFAULT NULL COMMENT '订单的状态，1、未付款 2、已付款,未发货 3、已发货,未确认 4、确认收货，交易成功 5、交易取消，订单关闭 6、交易结束，已评价',
    `createTime`  timestamp(0)                                                   NULL     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `startTime`   timestamp(0)                                                   NULL     DEFAULT NULL COMMENT '服务启用时间',
    `endTime`     timestamp(0)                                                   NULL     DEFAULT NULL COMMENT '服务关闭时间',
    `updateTime`  timestamp(0)                                                   NULL     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
    `otherInfo`   varchar(1024) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL     DEFAULT '' COMMENT '拓展字段',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `multi_key_status_time` (`status`, `createTime`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 27
  CHARACTER SET = utf8mb3
  COLLATE = utf8mb3_bin
  ROW_FORMAT = Compact;

-- ----------------------------
-- Records of api_order
-- ----------------------------
INSERT INTO `api_order`
VALUES (18, 3, 003, 1, 1, 3, '127.0.0.1', 2, '2023-11-07 14:32:00', NULL, NULL, '2023-11-07 23:59:01', NULL, NULL,
        '2023-11-07 14:32:00', 'Tue Nov 07 23:59:00 CST 2023');
INSERT INTO `api_order`
VALUES (19, 2, 003, 2, 1, 2, '127.0.0.1', 2, '2023-11-07 14:36:35', NULL, NULL, '2023-11-07 23:59:01', NULL, NULL,
        '2023-11-07 14:36:35', 'Tue Nov 07 23:59:00 CST 2023');
INSERT INTO `api_order`
VALUES (20, 2, 003, 3, 1, 2, '127.0.0.1', 2, '2023-11-07 18:54:18', NULL, NULL, '2023-11-07 23:59:00', NULL, NULL,
        '2023-11-07 18:54:18', 'Tue Nov 07 23:59:00 CST 2023');
INSERT INTO `api_order`
VALUES (21, 4, 003, 2, 2, 4, '127.0.0.1', 2, '2023-11-07 23:30:16', NULL, NULL, '2023-11-07 23:59:00', NULL, NULL,
        '2023-11-07 23:30:16', 'Tue Nov 07 23:59:00 CST 2023');
INSERT INTO `api_order`
VALUES (22, 2, 003, 1, 1, 5, '127.0.0.1', 2, '2023-11-08 22:44:43', NULL, NULL, '2023-11-08 23:59:01', NULL, NULL,
        '2023-11-08 22:44:43', 'Wed Nov 08 23:59:00 CST 2023');
INSERT INTO `api_order`
VALUES (23, 1, 003, 1, 1, 1, '127.0.0.1', 2, '2023-11-09 16:08:10', NULL, NULL, '2023-11-09 23:59:01', NULL, NULL,
        '2023-11-09 16:08:10', 'Thu Nov 09 23:59:00 CST 2023');
INSERT INTO `api_order`
VALUES (24, 1, 003, 1, 1, 1, '127.0.0.1', 2, '2023-11-15 20:03:41', NULL, NULL, '2023-11-15 23:59:01', NULL, NULL,
        '2023-11-15 20:03:41', 'Wed Nov 15 23:59:00 CST 2023');
INSERT INTO `api_order`
VALUES (25, 1, 003, 1, 1, 1, '127.0.0.1', 2, '2023-11-16 20:39:56', NULL, NULL, '2023-11-16 23:59:00', NULL, NULL,
        '2023-11-16 20:39:56', 'Thu Nov 16 23:59:00 CST 2023');
INSERT INTO `api_order`
VALUES (26, 1, 003, 1, 1, 1, '127.0.0.1', 2, '2023-11-17 13:50:19', NULL, NULL, '2023-11-17 23:59:00', NULL, NULL,
        '2023-11-17 13:50:19', 'Fri Nov 17 23:59:00 CST 2023');

-- ----------------------------
-- Table structure for interface_info
-- ----------------------------
DROP TABLE IF EXISTS `interface_info`;
CREATE TABLE `interface_info`
(
    `id`             bigint(0)                                                      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`           varchar(256) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci  NOT NULL COMMENT '名称',
    `description`    varchar(256) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci  NULL     DEFAULT NULL COMMENT '描述',
    `url`            varchar(512) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci  NOT NULL COMMENT '接口地址',
    `requestParams`  text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci          NOT NULL COMMENT '请求参数',
    `requestHeader`  text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci          NULL COMMENT '请求头',
    `responseHeader` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci          NULL COMMENT '响应头',
    `status`         int(0)                                                         NOT NULL DEFAULT 0 COMMENT '接口状态（0-关闭，1-开启）',
    `method`         varchar(256) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci  NOT NULL COMMENT '请求类型',
    `userId`         bigint(0)                                                      NOT NULL DEFAULT 1 COMMENT '创建人',
    `image`          varchar(512) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci  NULL     DEFAULT NULL COMMENT '接口图片',
    `price`          int(0)                                                         NOT NULL DEFAULT 0 COMMENT '价格（分）',
    `isAD`           int(0)                                                         NULL     DEFAULT 0 COMMENT '广告推广力度，0-100',
    `category`       varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci  NULL     DEFAULT NULL COMMENT '类目名称',
    `otherInfo`      varchar(1024) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL     DEFAULT NULL COMMENT '拓展信息',
    `createTime`     datetime(0)                                                    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updateTime`     datetime(0)                                                    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
    `isDelete`       tinyint(0)                                                     NOT NULL DEFAULT 0 COMMENT '是否删除(0-未删, 1-已删)',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 6
  CHARACTER SET = utf8mb3
  COLLATE = utf8mb3_general_ci COMMENT = '接口信息'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of interface_info
-- ----------------------------
INSERT INTO `interface_info`
VALUES (1, '接口用户添加测试', '土味情话', 'https://api.uomg.com/api/rand.qinghua', '', '', '', 1, 'get', 1, NULL, 1, 0,
        NULL, NULL, '2023-10-24 10:29:54', '2023-11-08 22:48:56', 0);
INSERT INTO `interface_info`
VALUES (2, '接口用户添加测试2', 'url描述2', 'https://api.uomg.com/api/rand.qinghua', '', '', '', 1, 'get/post', 1, NULL,
        1, 0, NULL, NULL, '2023-10-24 15:30:19', '2023-11-09 20:17:31', 0);
INSERT INTO `interface_info`
VALUES (3, '接口用户添加测试3', 'url描述3', 'http://blog.hekmatyar.cn', '', '', '', 0, 'get', 1, NULL, 0, 0, NULL, NULL,
        '2023-10-25 09:58:49', '2023-10-25 09:58:49', 0);
INSERT INTO `interface_info`
VALUES (4, '接口用户添加测试4', 'url描述4', 'http://blog.hekmatyar.cn', '', '', '', 0, 'get', 1, NULL, 0, 0, NULL, NULL,
        '2023-10-25 13:11:42', '2023-10-25 13:11:42', 0);
INSERT INTO `interface_info`
VALUES (5, '接口用户添加测试4', 'url描述4', 'http://blog.hekmatyar.cn', '', '', '', 0, 'get', 1, NULL, 0, 0, NULL, NULL,
        '2023-10-25 13:12:33', '2023-10-25 13:12:33', 0);

-- ----------------------------
-- Table structure for pay_order
-- ----------------------------
DROP TABLE IF EXISTS `pay_order`;
CREATE TABLE `pay_order`
(
    `id`             bigint(0)                                                      NOT NULL AUTO_INCREMENT COMMENT 'id',
    `bizOrderNo`     bigint(0)                                                      NULL     DEFAULT NULL COMMENT '业务订单号',
    `payOrderNo`     bigint(0)                                                      NULL     DEFAULT NULL COMMENT '支付单号',
    `bizUserId`      bigint(0)                                                      NOT NULL COMMENT '支付用户id',
    `amount`         int(0)                                                         NOT NULL COMMENT '支付金额，单位分',
    `payType`        tinyint(0)                                                     NOT NULL DEFAULT 5 COMMENT '支付类型，1：h5,2:小程序，3：公众号，4：扫码，5：余额支付，6：充值',
    `status`         tinyint(0)                                                     NOT NULL DEFAULT 0 COMMENT '支付状态，0：待提交，1:待支付，2：支付超时或取消，3：支付成功',
    `couponId`       bigint(0)                                                      NULL     DEFAULT NULL COMMENT '优惠券id',
    `paySuccessTime` datetime(0)                                                    NULL     DEFAULT NULL COMMENT '支付成功时间',
    `payOverTime`    datetime(0)                                                    NOT NULL COMMENT '支付超时时间',
    `expandJson`     varchar(1024) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '拓展字段',
    `payChannelCode` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci   NULL     DEFAULT '0' COMMENT '支付渠道编码',
    `createTime`     datetime(0)                                                    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updateTime`     datetime(0)                                                    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
    `resultCode`     varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci   NULL     DEFAULT '' COMMENT '第三方返回业务码',
    `resultMsg`      varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci   NULL     DEFAULT '' COMMENT '第三方返回提示信息',
    `qrCodeUrl`      varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci  NULL     DEFAULT NULL COMMENT '支付二维码链接',
    `creater`        bigint(0)                                                      NOT NULL DEFAULT 0 COMMENT '创建人',
    `updater`        bigint(0)                                                      NOT NULL DEFAULT 0 COMMENT '更新人',
    `isDelete`       bit(1)                                                         NOT NULL DEFAULT b'0' COMMENT '逻辑删除',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `pay_order_no` (`payOrderNo`) USING BTREE,
    UNIQUE INDEX `biz_order_no` (`bizOrderNo`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 20
  CHARACTER SET = utf8mb3
  COLLATE = utf8mb3_general_ci COMMENT = '支付订单'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of pay_order
-- ----------------------------
INSERT INTO `pay_order`
VALUES (1, 0, 0, 2, 10, 6, 3, NULL, NULL, '2023-11-02 15:11:58', '', '0', '2023-11-02 14:56:58', '2023-11-07 22:30:49',
        '', '', NULL, 0, 0, b'0');
INSERT INTO `pay_order`
VALUES (3, NULL, NULL, 1, 10, 6, 3, NULL, '2023-11-02 15:05:53', '2023-11-02 15:20:53', '', '0', '2023-11-02 15:05:53',
        '2023-11-02 15:05:53', '', '', NULL, 0, 0, b'0');
INSERT INTO `pay_order`
VALUES (4, NULL, NULL, 1, 10, 6, 3, NULL, '2023-11-02 15:39:52', '2023-11-02 15:54:52', '', '0', '2023-11-02 15:39:52',
        '2023-11-02 15:39:52', '', '', NULL, 0, 0, b'0');
INSERT INTO `pay_order`
VALUES (5, NULL, NULL, 1, 10, 6, 3, NULL, '2023-11-02 15:40:41', '2023-11-02 15:55:41', '', '0', '2023-11-02 15:40:40',
        '2023-11-02 15:40:40', '', '', NULL, 0, 0, b'0');
INSERT INTO `pay_order`
VALUES (6, NULL, NULL, 1, 10, 6, 3, NULL, '2023-11-02 15:43:00', '2023-11-02 15:58:00', '', '0', '2023-11-02 15:42:59',
        '2023-11-02 15:42:59', '', '', NULL, 0, 0, b'0');
INSERT INTO `pay_order`
VALUES (14, 18, NULL, 1, 3, 5, 3, NULL, NULL, '2023-11-07 23:59:01', '', '0', '2023-11-07 23:32:27',
        '2023-11-07 23:32:27', '', '', NULL, 1, 0, b'0');
INSERT INTO `pay_order`
VALUES (15, 19, NULL, 1, 2, 5, 3, NULL, NULL, '2023-11-07 23:59:01', '', '0', '2023-11-07 23:32:27',
        '2023-11-07 23:32:27', '', '', NULL, 1, 0, b'0');
INSERT INTO `pay_order`
VALUES (16, 20, NULL, 1, 2, 5, 3, NULL, NULL, '2023-11-07 23:59:01', '', '0', '2023-11-07 23:32:27',
        '2023-11-07 23:32:27', '', '', NULL, 1, 0, b'0');
INSERT INTO `pay_order`
VALUES (17, 21, NULL, 2, 4, 5, 3, NULL, NULL, '2023-11-07 23:59:01', '', '0', '2023-11-07 23:32:27',
        '2023-11-07 23:32:27', '', '', NULL, 2, 0, b'0');
INSERT INTO `pay_order`
VALUES (18, 22, NULL, 1, 2, 5, 3, NULL, NULL, '2023-11-08 23:59:00', '', '0', '2023-11-08 22:49:51',
        '2023-11-08 22:49:51', '', '', NULL, 1, 0, b'0');
INSERT INTO `pay_order`
VALUES (19, 23, NULL, 1, 1, 5, 3, NULL, NULL, '2023-11-09 23:59:01', '', '0', '2023-11-09 20:51:24',
        '2023-11-09 20:51:24', '', '', NULL, 1, 0, b'0');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`
(
    `id`           bigint(0)                                                      NOT NULL AUTO_INCREMENT COMMENT 'id',
    `userName`     varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NULL     DEFAULT NULL COMMENT '用户昵称',
    `userAccount`  varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NOT NULL COMMENT '账号',
    `userAvatar`   varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL     DEFAULT NULL COMMENT '用户头像',
    `gender`       tinyint(0)                                                     NULL     DEFAULT NULL COMMENT '性别',
    `userRole`     varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NOT NULL DEFAULT 'user' COMMENT '用户角色：user / admin',
    `userPassword` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NOT NULL COMMENT '密码',
    `accessKey`    varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NULL     DEFAULT NULL COMMENT 'accessKey',
    `secretKey`    varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NULL     DEFAULT NULL COMMENT 'secretKey',
    `unionId`      varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NULL     DEFAULT NULL COMMENT '微信开放平台id',
    `mpOpenId`     varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NULL     DEFAULT NULL COMMENT '公众号openId',
    `otherInfo`    varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL     DEFAULT NULL COMMENT '拓展信息',
    `createTime`   datetime(0)                                                    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updateTime`   datetime(0)                                                    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
    `isDelete`     tinyint(0)                                                     NOT NULL DEFAULT 0 COMMENT '是否删除',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_unionId` (`unionId`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 6
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '用户'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user`
VALUES (1, 'root', 'root', NULL, NULL, 'admin', '746d1b16e982da14bc00d657940f82dd', '0c589b511c7c126b32a105195032439b',
        '29276ec08e5f6ab62a80f7f34c06f718', NULL, NULL, NULL, '2023-10-23 00:30:32', '2023-11-02 10:23:02', 0);
INSERT INTO `user`
VALUES (2, NULL, 'testUser1', NULL, NULL, 'user', '746d1b16e982da14bc00d657940f82dd',
        '9f507201440adf1c050f58b294705ca1', '121012ae1321eaffd0f12af3a5300c81', NULL, NULL, NULL, '2023-10-23 14:08:21',
        '2023-10-23 14:08:21', 0);
INSERT INTO `user`
VALUES (3, '', 'testUser2', '', NULL, 'user', '746d1b16e982da14bc00d657940f82dd', NULL, NULL, NULL, NULL, NULL,
        '2023-10-23 15:54:50', '2023-10-23 19:09:00', 0);
INSERT INTO `user`
VALUES (4, 'banUser1', 'banUser1', NULL, NULL, 'ban', '746d1b16e982da14bc00d657940f82dd', NULL, NULL, NULL, NULL, NULL,
        '2023-10-23 18:33:04', '2023-10-23 18:33:04', 0);
INSERT INTO `user`
VALUES (5, NULL, 'User4', NULL, NULL, 'user', '746d1b16e982da14bc00d657940f82dd', '19ab239b4ba5748a2493e84055cb686a',
        'd9dd5fe5a2c1d846595ae1a9f491cb99', NULL, NULL, NULL, '2023-10-23 18:43:12', '2023-10-23 18:43:12', 0);

-- ----------------------------
-- Table structure for user_balance_payment
-- ----------------------------
DROP TABLE IF EXISTS `user_balance_payment`;
CREATE TABLE `user_balance_payment`
(
    `id`            bigint(0)                                                      NOT NULL AUTO_INCREMENT COMMENT 'id',
    `userId`        bigint(0)                                                      NOT NULL COMMENT '用户ID',
    `balance`       bigint(0)                                                      NOT NULL DEFAULT 30 COMMENT '用户余额',
    `frozenAmount`  bigint(0)                                                      NOT NULL DEFAULT 0 COMMENT '冻结资金-----用于退款等',
    `score`         bigint(0)                                                      NOT NULL DEFAULT 0 COMMENT '用户积分',
    `expenseAmount` bigint(0)                                                      NOT NULL DEFAULT 0 COMMENT '消费金额',
    `expenseScore`  bigint(0)                                                      NOT NULL DEFAULT 0 COMMENT '消费积分',
    `expenseCount`  bigint(0)                                                      NOT NULL DEFAULT 0 COMMENT '消费次数',
    `otherInfo`     varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL     DEFAULT NULL COMMENT '拓展信息',
    `createTime`    datetime(0)                                                    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updateTime`    datetime(0)                                                    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
    `isDelete`      tinyint(0)                                                     NOT NULL DEFAULT 0 COMMENT '是否删除',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_userId` (`userId`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 2
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '用户余额表'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_balance_payment
-- ----------------------------
INSERT INTO `user_balance_payment`
VALUES (1, 1, 100041, 0, 0, 0, 0, 0, NULL, '2023-11-02 11:15:03', '2023-11-02 11:15:06', 0);

SET FOREIGN_KEY_CHECKS = 1;
