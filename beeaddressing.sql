/*
SQLyog Ultimate v10.00 Beta1
MySQL - 8.0.21 : Database - beeaddressing
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`beeaddressing` /*!40100 DEFAULT CHARACTER SET utf8 */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `beeaddressing`;

/*Table structure for table `bee_city` */

DROP TABLE IF EXISTS `bee_city`;

CREATE TABLE `bee_city` (
  `city_id` int NOT NULL AUTO_INCREMENT COMMENT '城市id',
  `city_name` varchar(20) DEFAULT NULL COMMENT '城市名称',
  `timestamp` datetime DEFAULT NULL COMMENT '时间戳',
  PRIMARY KEY (`city_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `bee_city` */

/*Table structure for table `bee_county` */

DROP TABLE IF EXISTS `bee_county`;

CREATE TABLE `bee_county` (
  `county_id` int NOT NULL AUTO_INCREMENT COMMENT '地区ID',
  `county_name` varchar(20) DEFAULT NULL COMMENT '地区名称',
  `city_id` varchar(20) DEFAULT NULL COMMENT '城市ID',
  `timestamp` datetime DEFAULT NULL COMMENT '时间戳',
  PRIMARY KEY (`county_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `bee_county` */

/*Table structure for table `bee_store` */

DROP TABLE IF EXISTS `bee_store`;

CREATE TABLE `bee_store` (
  `store_id` bigint NOT NULL AUTO_INCREMENT COMMENT '商铺自增id',
  `city_id` int DEFAULT NULL COMMENT '城市id',
  `county_id` int DEFAULT NULL COMMENT '地区id',
  `store_location` varchar(100) DEFAULT NULL COMMENT '经纬度',
  `store_title` varchar(120) DEFAULT NULL COMMENT '商铺标题',
  `store_img` varchar(120) DEFAULT NULL COMMENT '商铺图片的url',
  `store_area` float DEFAULT NULL COMMENT '商铺面积',
  `store_address` varchar(300) DEFAULT NULL COMMENT '商铺地址',
  `store_rent` decimal(10,0) DEFAULT NULL COMMENT '商铺租金',
  `area_type` varchar(10) DEFAULT NULL COMMENT '面积单位',
  `rent_type` varchar(10) DEFAULT NULL COMMENT '租金单位',
  `publisher_name` varchar(10) DEFAULT NULL COMMENT '发布人姓名',
  `publisher_tel` varchar(11) DEFAULT NULL COMMENT '发布人联系方式',
  `rent_state` varchar(10) DEFAULT NULL COMMENT '出租状态',
  `is_favorite` tinyint(1) DEFAULT NULL COMMENT '是否收藏',
  `timestamp` datetime DEFAULT NULL COMMENT '时间戳',
  PRIMARY KEY (`store_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `bee_store` */

/*Table structure for table `bee_user` */

DROP TABLE IF EXISTS `bee_user`;

CREATE TABLE `bee_user` (
  `user_id` int NOT NULL AUTO_INCREMENT COMMENT '用户id',
  `user_name` varchar(20) DEFAULT NULL COMMENT '用户昵称',
  `user_tel` varchar(11) DEFAULT NULL COMMENT '用户手机号',
  `user_headUrl` varchar(120) DEFAULT NULL COMMENT '头像url',
  `user_pwd` varchar(32) DEFAULT NULL COMMENT '用户密码',
  `timestamp` datetime DEFAULT NULL COMMENT '时间戳',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `bee_user` */

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
