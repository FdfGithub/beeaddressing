package com.example.beeaddressing.service;

import com.example.beeaddressing.pojo.County;

import java.util.List;

public interface CountyService {
    List<County> getCounty(String cityId);
}
