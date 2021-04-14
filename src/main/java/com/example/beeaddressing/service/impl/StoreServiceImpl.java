package com.example.beeaddressing.service.impl;

import com.example.beeaddressing.api.GodApi;
import com.example.beeaddressing.dao.StoreMapper;
import com.example.beeaddressing.pojo.Store;
import com.example.beeaddressing.service.StoreService;
import com.example.beeaddressing.vo.Power;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class StoreServiceImpl implements StoreService {

    @Autowired
    private StoreMapper storeMapper;

    @Override
    public List<Store> findPage(String cityId, String countyId, Integer page, Integer size) {
        if (StringUtils.isEmpty(cityId)){
            throw new RuntimeException("未选择城市");
        }
        PageHelper.startPage(page, size);
        return storeMapper.findStores(cityId, countyId);
    }
    @Autowired
    private GodApi godApi;

    @Override
    public List<Power> findPowers(String cityId, String countyId) {
        List<Map<String, Object>> powers = storeMapper.findPowers(cityId, countyId);
        List<Power> p = Lists.newArrayList();
        Power center = new Power();
        center.setCount("0");
        //获取地区的经纬度
        String adcode = (String) powers.get(0).get("adcode");
        String county_name = (String)powers.get(0).get("county_name");
        List<Map> geocodes = (List<Map>)godApi.addressToCoordinate(county_name, adcode).get("geocodes");
        String[] centers = String.valueOf(geocodes.get(0).get("location")).split(",");
        center.setLat(centers[1]);
        center.setLng(centers[0]);
        p.add(center);
        for (Map<String, Object> power : powers) {
            Power power1 = new Power();
            power1.setCount(String.valueOf(power.get("score")));
            String[] locations = ((String) power.get("location")).split(",");
            power1.setLng(locations[0]);
            power1.setLat(locations[1]);
            p.add(power1);
        }
        return p;
    }
}
