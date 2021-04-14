package com.example.beeaddressing.pojo;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@Table(name = "bee_store")
public class Store {
    @Id
    private Long storeId; //商铺id

    private String cityId; //城市id

    private String countyId; //区id

    private String location; //经纬度

    private String storeTitle; //商铺标题

    private String storeImg; //商铺图片

    private Float storeArea; //商铺面积

    private String storeAddress; //商铺地址

    private Float storeRent; //商铺租金

    private String publisherName; //联系人姓名

    private String publisherTel; //发布人联系方式

    private String rentState; //出租状态

    private Float score; //评分值

    private LocalDateTime timestamp; //时间戳

    private String adcode;

    private String scorePoints;
}
