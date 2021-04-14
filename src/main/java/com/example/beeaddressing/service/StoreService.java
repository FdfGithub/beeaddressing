package com.example.beeaddressing.service;

import com.example.beeaddressing.pojo.Store;
import com.example.beeaddressing.vo.Power;

import java.util.List;

public interface StoreService {
    List<Store> findPage(String cityId,String countyId,Integer page,Integer size);

    List<Power> findPowers(String cityId,String countyId);
}
