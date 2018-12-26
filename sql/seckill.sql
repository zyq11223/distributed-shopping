/*
Navicat MySQL Data Transfer

Source Server         : localhost_3306
Source Server Version : 50619
Source Host           : localhost:3306
Source Database       : seckill

Target Server Type    : MYSQL
Target Server Version : 50619
File Encoding         : 65001

Date: 2018-10-22 12:12:55
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for goods
-- ----------------------------
DROP TABLE IF EXISTS `goods`;
CREATE TABLE `goods` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `goods_name` varchar(16) DEFAULT NULL COMMENT '商品名称',
  `goods_title` varchar(64) DEFAULT NULL COMMENT '商品标题',
  `goods_img` varchar(64) DEFAULT NULL,
  `goods_detail` longtext COMMENT '商品详情介绍',
  `goods_price` decimal(10,2) DEFAULT NULL COMMENT '单价',
  `goods_stock` int(11) DEFAULT NULL COMMENT '商品库存 -1表示无限制',
  `remoteAddr` varchar(50) DEFAULT NULL COMMENT '远程图片服务器IP+目录，如192.168.12.128/images',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Records of goods
-- ----------------------------
INSERT INTO `goods` VALUES ('1', 'iphoneX', 'Apple iPhone X(A1865) 64GB 银色 移动联通电信4G手机', 'http://192.168.58.133/images/phone/iphonex.png', 'Apple iPhoneX 64GB 银色 移动联通电信4G手机', '8765.00', '100', null);
INSERT INTO `goods` VALUES ('2', '华为Meta9', '华为Meta9 4GB', 'http://192.168.58.133/images/phone/meta10.png', '华为Meta9 4GB 银色', '3672.00', '114', null);

-- ----------------------------
-- Table structure for miaosha_goods
-- ----------------------------
DROP TABLE IF EXISTS `miaosha_goods`;
CREATE TABLE `miaosha_goods` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '秒杀的商品表',
  `goods_id` bigint(20) DEFAULT NULL COMMENT '商品id',
  `miaosha_price` decimal(10,2) NOT NULL COMMENT '秒杀价格',
  `stock_count` int(11) DEFAULT NULL COMMENT '库存数量',
  `start_date` datetime DEFAULT NULL COMMENT '秒杀开始时间',
  `end_date` datetime DEFAULT NULL COMMENT '秒杀结束时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Records of miaosha_goods
-- ----------------------------
INSERT INTO `miaosha_goods` VALUES ('1', '1', '0.01', '7', '2018-10-07 12:01:00', '2018-10-19 14:00:18');
INSERT INTO `miaosha_goods` VALUES ('2', '2', '0.01', '10', '2018-10-07 12:11:14', '2018-10-26 14:00:24');

-- ----------------------------
-- Table structure for miaosha_order
-- ----------------------------
DROP TABLE IF EXISTS `miaosha_order`;
CREATE TABLE `miaosha_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL,
  `order_id` bigint(20) DEFAULT NULL,
  `goods_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Records of miaosha_order
-- ----------------------------
INSERT INTO `miaosha_order` VALUES ('6', '18912349087', '12', '1');
INSERT INTO `miaosha_order` VALUES ('7', '18912349087', '13', '2');
INSERT INTO `miaosha_order` VALUES ('8', '18912349087', '14', '1');
INSERT INTO `miaosha_order` VALUES ('9', '18912349087', '15', '1');
INSERT INTO `miaosha_order` VALUES ('10', '18912349087', '16', '2');
INSERT INTO `miaosha_order` VALUES ('11', '18912349087', '17', '1');
INSERT INTO `miaosha_order` VALUES ('12', '18912349087', '18', '2');

-- ----------------------------
-- Table structure for miaosha_user
-- ----------------------------
DROP TABLE IF EXISTS `miaosha_user`;
CREATE TABLE `miaosha_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID，手机号码',
  `nickname` varchar(255) NOT NULL,
  `password` varchar(32) DEFAULT NULL COMMENT 'MD5(MD5(pass明文+固定salt)+salt)',
  `salt` varchar(10) DEFAULT NULL,
  `register_date` datetime DEFAULT NULL COMMENT '注册时间',
  `last_login_date` datetime NOT NULL COMMENT '上次登录时间',
  `login_count` int(11) DEFAULT NULL COMMENT '登录次数',
  PRIMARY KEY (`id`,`last_login_date`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=18912349088 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Records of miaosha_user
-- ----------------------------
INSERT INTO `miaosha_user` VALUES ('18912349087', 'Yin', 'b7797cce01b4b131b433b6acf4add449', '1a2b3c4d', '2018-06-11 18:20:22', '2018-06-11 18:20:29', '1');

-- ----------------------------
-- Table structure for order_info
-- ----------------------------
DROP TABLE IF EXISTS `order_info`;
CREATE TABLE `order_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL,
  `goods_id` bigint(20) unsigned zerofill DEFAULT NULL COMMENT '商品id',
  `delivery_addr_id` bigint(20) DEFAULT NULL COMMENT '收货地址id',
  `goods_name` varchar(16) DEFAULT NULL,
  `goods_count` int(11) DEFAULT NULL,
  `goods_price` decimal(10,2) DEFAULT NULL,
  `order_channel` tinyint(4) DEFAULT NULL COMMENT '1PC  2 Android  3  iOS  ',
  `status` tinyint(4) DEFAULT NULL COMMENT '订单状态  0新建未支付 1已支付 2 已发货 3 已收货 4已退款 5 已完成',
  `create_date` datetime DEFAULT NULL,
  `pay_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Records of order_info
-- ----------------------------
INSERT INTO `order_info` VALUES ('12', '18912349087', '00000000000000000001', null, 'iphoneX', '1', '0.01', '1', '0', '2018-10-18 15:03:19', null);
INSERT INTO `order_info` VALUES ('13', '18912349087', '00000000000000000002', null, '华为Meta9', '1', '0.01', '1', '0', '2018-10-18 15:38:17', null);
INSERT INTO `order_info` VALUES ('14', '18912349087', '00000000000000000001', null, 'iphoneX', '1', '0.01', '1', '0', '2018-10-18 15:44:15', null);
INSERT INTO `order_info` VALUES ('15', '18912349087', '00000000000000000001', null, 'iphoneX', '1', '0.01', '1', '0', '2018-10-18 16:40:13', null);
INSERT INTO `order_info` VALUES ('16', '18912349087', '00000000000000000002', null, '华为Meta9', '1', '0.01', '1', '0', '2018-10-18 16:40:47', null);
INSERT INTO `order_info` VALUES ('17', '18912349087', '00000000000000000001', null, 'iphoneX', '1', '0.01', '1', '0', '2018-10-19 09:42:42', null);
INSERT INTO `order_info` VALUES ('18', '18912349087', '00000000000000000002', null, '华为Meta9', '1', '0.01', '1', '0', '2018-10-22 09:52:31', null);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(11) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('1', 'yhf');
INSERT INTO `user` VALUES ('2', '222');
