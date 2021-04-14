package com.example.beeaddressing.form;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FilterForm {
    private String keyword; //关键词
    private BigDecimal minPrice; //最小价格
    private BigDecimal maxPrice; //最大价格
    private Float minArea; //最小面积
    private Float maxArea; //最大面积
}
