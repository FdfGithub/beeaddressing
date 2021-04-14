package com.example.beeaddressing.model;

import com.example.beeaddressing.api.GodApi;
import com.example.beeaddressing.api.GodProperties;
import com.example.beeaddressing.dao.CountyMapper;
import com.example.beeaddressing.dao.StoreMapper;
import com.example.beeaddressing.dao.VillageMapper;
import com.example.beeaddressing.pojo.County;
import com.example.beeaddressing.pojo.Store;
import com.example.beeaddressing.pojo.Village;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/*
1、人口：总人口，年龄结构，男女比例，未婚人数
2、距离：
消费者和选址点之间的距离
消费者：住宅区、酒店、写字楼、休闲娱乐、教育、餐厅、交通站点、旅游点、单身公寓
步行时间的比较
3、周边环境
住宅区、酒店、写字楼、休闲娱乐、教育、餐厅、交通站点、旅游点的个数
竞争者（便利店）的个数
4、消费能力
*/
@Component
@Slf4j
public class ScoreModel {

    @Autowired
    private GodApi godApi;

    @Autowired
    private GodProperties godProperties;

    @Autowired
    private StoreMapper storeMapper;

    public void addLocation() {
        List<Store> stores = storeMapper.selectAll();
        for (Store store : stores) {
            Map map = godApi.addressToCoordinate(store.getStoreAddress(), store.getAdcode());
            if ("0".equals(map.get("count"))) {
                continue;
            }
            store.setLocation((String) ((List<Map>) map.get("geocodes")).get(0).get("location"));
            log.info("选址点id：{},经纬度：{}", store.getStoreId(), store.getLocation());
        }
        //批量修改
        storeMapper.updateStoresBatch(stores);
    }
    @Autowired
    private VillageMapper villageMapper;

    public void addLocationVillage(){
        List<Village> villages = villageMapper.findVillages();

        for (Village village1 : villages) {
            County county = countyMapper.selectByPrimaryKey(village1.getCountyId());
            if (county == null){
                continue;
            }
            String adcode = county.getAdcode();
            if (StringUtils.isEmpty(adcode)){
                continue;
            }
            Map map = godApi.addressToCoordinate(village1.getVillageName(),adcode);
            if ("0".equals(map.get("count"))) {
                continue;
            }
            village1.setLocation((String) ((List<Map>) map.get("geocodes")).get(0).get("location"));
            log.info("选址点id：{},经纬度：{}", village1.getVillageId(), village1.getLocation());
            villageMapper.updateByPrimaryKeySelective(village1);
        }


    }




//    @Scheduled(cron = "0 0 0 * * ?")
    public void execute() throws InterruptedException {
//        PageHelper.startPage(2,100);
        //获取所有的选址点信息
//        List<Store> stores = storeMapper.findStores(null, null);
        List<Store> stores = storeMapper.findStoresByCountyId("minhou");
        Set<Store> set = Sets.newTreeSet(Comparator.comparing(Store::getCountyId));
        set.addAll(stores);
        //将选址点按照地区进行分组
        List<Map<String, Object>> scores = Lists.newArrayList();
        for (Store store : set) {
            List<Store> filterStores = filterByCountyId(stores, store.getCountyId());
            scores.addAll(compute(filterStores));
        }
        log.info("操作完成");
        //批量存储到数据库
        storeMapper.updateStores(scores);
    }

    @Autowired
    private CountyMapper countyMapper;

    public List<Map<String, Object>> compute(List<Store> stores) {
        //获取该地区下的参考数据
        //获取当地所有的便利店
        String adcode = stores.get(0).getAdcode();
        float avgRent = 0;
        for (Store store : stores) {
            avgRent += store.getStoreRent() / store.getStoreArea();
        }
        avgRent /= stores.size();//平均租金价格  每平米
        List<Map> conveniences = godApi.getAll(GodProperties.POI.Convenience.getName()
                , GodProperties.POI.Convenience.getType()
                , adcode);
        int house = 0;
        int food = 0;
        int office = 0;
        int relaxation = 0;
        int school = 0;
        int traffic = 0;
        int shopping = 0;
        Map<String, Integer> references = Maps.newHashMap();
        for (Map convenience : conveniences) {
            Map<String, Integer> around = around((String) convenience.get("location"));
            house += around.get(GodProperties.POI.House.toString().toLowerCase());
            food += around.get(GodProperties.POI.Food.toString().toLowerCase());
            office += around.get(GodProperties.POI.Office.toString().toLowerCase());
            relaxation += around.get(GodProperties.POI.Relaxation.toString().toLowerCase());
            school += around.get(GodProperties.POI.School.toString().toLowerCase());
            traffic += around.get(GodProperties.POI.Traffic.toString().toLowerCase());
            shopping += around.get(GodProperties.POI.Shopping.toString().toLowerCase());
        }
        int size = conveniences.size();
        references.put(GodProperties.POI.House.toString().toLowerCase(), house / size);
        references.put(GodProperties.POI.Food.toString().toLowerCase(), food / size);
        references.put(GodProperties.POI.Office.toString().toLowerCase(), office / size);
        references.put(GodProperties.POI.Relaxation.toString().toLowerCase(), relaxation / size);
        references.put(GodProperties.POI.School.toString().toLowerCase(), school / size);
        references.put(GodProperties.POI.Traffic.toString().toLowerCase(), traffic / size);
        references.put(GodProperties.POI.Shopping.toString().toLowerCase(), shopping / size);

        List<Map<String, Object>> scores = Lists.newArrayList();
        for (Store store : stores) {
            Map<String, Object> score = Maps.newHashMap();
            float distance = distance(store.getLocation());
            float surrounds = surrounds(store.getLocation(), references);
            float population = population();
            float housePrice = housePrice(store.getStoreRent() / store.getStoreArea(), avgRent);
            float value = (distance + surrounds + population + housePrice) / 4;
            score.put("id", store.getStoreId());
            score.put("scorePoints", String.format("distance:%s,surrounds:%s" +
                            ",population:%s,housePrice:%s"
                    , (float) (Math.round(distance * 10)) / 10
                    , (float) (Math.round(surrounds * 10)) / 10
                    , (float) (Math.round(population * 10)) / 10
                    , (float) (Math.round(housePrice * 10)) / 10));
            score.put("value", (float) (Math.round(value * 10)) / 10);
            scores.add(score);
            log.info("id:{},score:{}", store.getStoreId(), score);
        }
        return scores;
    }


    //指标1：实地考察，获取有效人流量
    //寻找大型商场
    //热力图：人流量分布，力度大些
    //甜甜圈图（年龄结构）
    //住宅区决定人口的分布
    public float population() {
       return (float) Math.random() * 7 + 3;
    }

    //指标2：分析距离，住宿、居民区、酒店到选址点的距离
    //L1：500米   L2：1000米
    //根据分段效用函数分析距离指标
    //地图：展现各个住宿点和选址点的路径（标出路程和步行时长等数据）
    public float distance(String location) {
        //分析住宿区到选址点的距离
        //封装500米范围内所有的住宿区
        List<Map> pois = godApi.getAll(location, GodProperties.POI.House.getName()
                , GodProperties.POI.House.getType()
                , godProperties.getRadius());
        if (pois.size() == 0) {
            return 0f;
        }
        int distance = 0;
        for (Map poi : pois) {
            distance += godApi.getDistance(location, (String) poi.get("location"));
        }
        distance /= pois.size();
        //分段效用函数
        //消费者到选址点的最小临界距离
        int L1 = 300;
        //消费者到选址点的最大临界距离
        int L2 = 500;
        if (distance <= L1) {
            return 10f;
        } else if (distance < L2) {
            return 10f * (1 - distance / (float) L2);
        }
        return 0f;
    }


    //指标3：周边环境：饼图
    public float surrounds(String location, Map<String, Integer> references) {
        Map<String, Integer> around = around(location);
        float sum = 0f;
        for (GodProperties.POI poi : GodProperties.POI.values()) {
            if (poi == GodProperties.POI.Convenience) {
                continue;
            }
            int current = around.get(poi.toString().toLowerCase());
            int reference = references.get(poi.toString().toLowerCase());
            if (current < reference) {
                sum += 10 * (current / (float) reference);
            } else {
                sum += 10f;
            }
        }
        return sum / references.size();
    }


    public Map<String, Integer> around(String location) {
        Map<String, Integer> surrounds = Maps.newHashMap();
        for (GodProperties.POI poi : GodProperties.POI.values()) {
            if (poi == GodProperties.POI.Convenience) {
                continue;
            }
            surrounds.put(poi.toString().toLowerCase(), Integer.parseInt(
                    (String) godApi.getAroundPOI(location, poi.getName(), poi.getType()
                            , godProperties.getRadius(), godProperties.getFirstPage()).get("count")
            ));
        }
        return surrounds;
    }


    //指标4：消费能力：折线图（各年龄结构的消费能力）
    //大型商场的数量决定消费能力
//    public float consume(String location, Map<String, Integer> references) {
//        int current = Integer.parseInt((String) godApi.getAroundPOI(location, GodProperties.POI.Shopping.getName(),
//                GodProperties.POI.Shopping.getType(), godProperties.getRadius(), godProperties.getFirstPage()).get("count"));
//        int reference = references.get(GodProperties.POI.Shopping.toString().toLowerCase());
//        if (current < reference) {
//            return 10 * (1 - current / (float) reference);
//        }
//        return 0f;
//    }

    //指标5：房价
    public float housePrice(float rent, float avgRent) {
        if (rent < avgRent) {
            return 10f * (rent / avgRent);
        }
        return 10f;
    }

    private List<Store> filterByCountyId(List<Store> stores, String countyId) {
        return stores.stream().filter(store -> store.getCountyId().equals(countyId))
                .collect(Collectors.toList());
    }
}
