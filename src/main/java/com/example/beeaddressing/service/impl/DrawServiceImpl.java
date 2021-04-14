package com.example.beeaddressing.service.impl;

import com.example.beeaddressing.api.GodApi;
import com.example.beeaddressing.api.GodProperties;
import com.example.beeaddressing.dao.CountyMapper;
import com.example.beeaddressing.dao.StoreMapper;
import com.example.beeaddressing.dao.VillageMapper;
import com.example.beeaddressing.pojo.Store;
import com.example.beeaddressing.pojo.Village;
import com.example.beeaddressing.service.DrawService;
import com.example.beeaddressing.util.JSONUtil;
import com.example.beeaddressing.vo.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DrawServiceImpl implements DrawService {

    //指标1：实地考察，获取有效人流量
    //热力图：人流量分布，力度大些
    //甜甜圈图（年龄结构）
    //住宅区决定人口的分布
    //人口分布，精确到乡镇级别
    //第一步：画出该地区下的人口热力分布图
    //第二步：画出该选址点的位置
    //分值
    @Autowired
    private StoreMapper storeMapper;

    @Autowired
    private CountyMapper countyMapper;

    @Autowired
    private VillageMapper villageMapper;

    @Override
    public PopulationVo population(Long storeId) {
        Store store = storeMapper.selectByPrimaryKey(storeId);
        //查找所有village的county等于County的county
        PopulationVo vo = new PopulationVo();
        vo.setPowers(getPowers(store.getCountyId()));
        return vo;
    }

    private List<Power> getPowers(String countyId) {
        Village params = new Village();
        params.setCountyId(countyId);
        List<Village> villages = villageMapper.select(params);
        List<Power> powers = Lists.newArrayList();
        for (Village village : villages) {
            Power power = new Power();
            String[] locations = village.getLocation().split(",");
            power.setLng(locations[0]);
            power.setLat(locations[1]);
            power.setCount(String.valueOf(village.getPopulation()));
            powers.add(power);
        }
        return powers;
    }

    @Autowired
    private GodApi godApi;


//    @Override
//    public Distance distance(Long storeId) {
//        Store store = storeMapper.selectByPrimaryKey(storeId);
//        List<Map> pois = godApi.getAll(store.getLocation()
//                , GodProperties.POI.House.getName()
//                , GodProperties.POI.House.getType()
//                , "500");
//        Distance distance = new Distance();
//        distance.setHouses(assembleHouses(store.getLocation()));
//        distance.setMaxDistance(500);
//        distance.setMinDistance(300);
//        return distance;
//    }

//    private List<Map<String, Object>> assembleHouses(String location) {
//        List<Map<String, Object>> houses = Lists.newArrayList();
//        List<Map> list = godApi.getAll(location, GodProperties.POI.House.getName(),
//                GodProperties.POI.House.getType(), "500");
//        for (Map map : list) {
//            Map<String, Object> house = Maps.newHashMap();
//            String house_location = (String) map.get("location");
//            Map route = (Map) godApi.getWalking(house_location, location).get("route");
//            List<Map> paths = (List<Map>) route.get("paths");
//            house.put("distance", Integer.parseInt((String) paths.get(0).get("distance")));
//            house.put("duration", Integer.parseInt((String) paths.get(0).get("duration")));
//            house.put("location", house_location);
//            houses.add(house);
//        }
//        return houses;
//    }


    @Override
    public List<Map<String, Object>> surrounds(Long storeId) {
        Store store = storeMapper.selectByPrimaryKey(storeId);
        List<Map<String, Object>> surrounds = Lists.newArrayList();
        for (GodProperties.POI poi : GodProperties.POI.values()) {
            List<Map> list = godApi.getAll(store.getLocation(), poi.getName(), poi.getType()
                    , "500");
            for (Map map : list) {
                Map<String, Object> type = Maps.newHashMap();
                type.put("location", map.get("location"));
                type.put("type", poi.toString().toLowerCase());
                if (poi == GodProperties.POI.House){
                    String house_location = (String) map.get("location");
                    Map route = (Map) godApi.getWalking(house_location, store.getLocation()).get("route");
                    List<Map> paths = (List<Map>) route.get("paths");
                    type.put("distance",Integer.parseInt((String) paths.get(0).get("distance")));
                }
                surrounds.add(type);
            }
        }
        return surrounds;
    }


    @Override
    public HousePrice housePrice(Long storeId) {
        //查找该选址点所在地区的所有选址点
        List<Store> stores = storeMapper.findStoresByStoreId(storeId);
        float avgRent = 0f;
        float rent = 0f;
        for (Store store : stores) {
            if (rent == 0f && store.getStoreId().equals(storeId)){
                rent = store.getStoreRent() / store.getStoreArea() / 30;
            }
            avgRent += store.getStoreRent() / store.getStoreArea() / 30;
        }
        avgRent /= stores.size();
        HousePrice housePrice = new HousePrice();
        housePrice.setAvgRent(avgRent);
        housePrice.setRent(rent);
        return housePrice;
    }


    public Map<String,Object> leidatu(Long storeId){
        Store store = storeMapper.selectByPrimaryKey(storeId);
        return JSONUtil.StringToMap(store.getScorePoints());
    }
}
