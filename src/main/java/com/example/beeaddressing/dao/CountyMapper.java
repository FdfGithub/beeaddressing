package com.example.beeaddressing.dao;

import com.example.beeaddressing.pojo.County;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface CountyMapper extends Mapper<County> {
    List<County> findCountiesByCityId(String cityId);
}
