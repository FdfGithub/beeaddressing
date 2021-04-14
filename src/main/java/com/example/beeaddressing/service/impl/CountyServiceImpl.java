package com.example.beeaddressing.service.impl;

import com.example.beeaddressing.dao.CountyMapper;
import com.example.beeaddressing.pojo.County;
import com.example.beeaddressing.service.CountyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

@Service
public class CountyServiceImpl implements CountyService {

    @Autowired
    private CountyMapper countyMapper;

    @Override
    public List<County> getCounty(String cityId) {
        List<County> counties = countyMapper.findCountiesByCityId(cityId);
        Assert.notEmpty(counties,"城市id错误");
        return counties;
    }
}
