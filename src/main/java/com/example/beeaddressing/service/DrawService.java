package com.example.beeaddressing.service;

import com.example.beeaddressing.vo.Distance;
import com.example.beeaddressing.vo.HousePrice;
import com.example.beeaddressing.vo.PopulationVo;
import com.example.beeaddressing.vo.Surrounds;

import java.util.List;
import java.util.Map;

public interface DrawService {
    PopulationVo population(Long storeId);

//    Distance distance(Long storeId);

    List<Map<String, Object>> surrounds(Long storeId);

    HousePrice housePrice(Long storeId);

    Map<String,Object> leidatu(Long storeId);
}
