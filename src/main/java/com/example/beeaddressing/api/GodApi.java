package com.example.beeaddressing.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import javax.swing.plaf.PanelUI;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class GodApi {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private GodProperties godProperties;

    //POI搜索：周边搜索
    public Map getAroundPOI(String location, String keywords
            , String types, String radius, String page) {
        Map<String, String> params = Maps.newHashMap();
        params.put("location", location);
        params.put("keywords", keywords);
        params.put("types", types);
        params.put("radius", radius);
        params.put("page", page);
        String url = buildUrl("http://restapi.amap.com/v3/place/around?", params);
        return restTemplate.getForEntity(url, Map.class).getBody();
    }


    //步行路径规划
    public Map getWalking(String origin, String destination) {
        Map<String, String> params = Maps.newHashMap();
        params.put("origin", origin);
        params.put("destination", destination);
        String url = buildUrl("https://restapi.amap.com/v3/direction/walking?", params);
        return restTemplate.getForEntity(url, Map.class).getBody();
    }


    public Map getAroundPOI(String keywords,String types,String city,String page){
        Map<String,String> params = Maps.newHashMap();
        params.put("keywords",keywords);
        params.put("types",types);
        params.put("city",city);
        params.put("page",page);
        String url = buildUrl("https://restapi.amap.com/v3/place/text?", params);
        return restTemplate.getForEntity(url,Map.class).getBody();
    }

    //获取指定城市下的POI数据
    public List<Map> getAll(String keywords, String types, String city){
        Map aroundPOI = getAroundPOI(keywords, types, city, godProperties.getFirstPage());
        int count = Integer.parseInt((String) aroundPOI.get("count"));
        List<Map> pois = (List<Map>) aroundPOI.get("pois");
        if (count <= 20){
            return pois;
        }
        for (int i = 21; i < count; i += 20) {
            Map poi = getAroundPOI(keywords, types, city,
                    String.valueOf(i));
            List<Map> temp = (List<Map>) poi.get("pois");
            if (temp.size() == 0) {
                continue;
            }
            pois.addAll(temp);
        }
        return pois;
    }


    public int getDistance(String origin, String destination){
        Map route = (Map) getWalking(origin, destination).get("route");
        List<Map> paths = (List<Map>) route.get("paths");
        return Integer.parseInt((String) paths.get(0).get("distance"));
    }



    //地理编码  结构化地址转换成经纬度坐标
    public Map addressToCoordinate(String address,String city) {
        Map<String, String> params = Maps.newHashMap();
        params.put("address", address);
        params.put("city", city);
        String url = buildUrl("https://restapi.amap.com/v3/geocode/geo?", params);
        return restTemplate.getForEntity(url, Map.class).getBody();
    }


    public Map coordinateToAddress(String location, String poitype, String radius) {
        Map<String, String> params = Maps.newHashMap();
        params.put("roadlevel", "0");
        params.put("location", location);
        params.put("poitype", poitype);
        params.put("radius", radius);
        String url = buildUrl("https://restapi.amap.com/v3/geocode/regeo?", params);
        return restTemplate.getForEntity(url, Map.class).getBody();
    }



    public List<Map> getAll(String location,String keywords
            , String types, String radius){
        Map aroundPOI = getAroundPOI(location
                , keywords, types
                , godProperties.getRadius()
                , godProperties.getFirstPage());
        int count = Integer.parseInt((String) aroundPOI.get("count"));
        List<Map> pois = (List<Map>) aroundPOI.get("pois");
        if (count <= 20){
            return pois;
        }
        for (int i = 21; i < count; i += 20) {
            Map poi = getAroundPOI(location, keywords, types, radius,
                    String.valueOf(i));
            List<Map> temp = (List<Map>) poi.get("pois");
            if (temp.size() == 0) {
                continue;
            }
            pois.addAll(temp);
        }
        return pois;
    }


    //构建url
    private String buildUrl(String baseUrl, Map<String, String> params) {
        Assert.notEmpty(params, "参数不能为空");
        StringBuilder url = new StringBuilder();
        url.append(baseUrl).append("key=").append(godProperties.getKey())
                .append("&extensions=").append("all");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            url.append("&").append(entry.getKey()).append("=").append(entry.getValue());
        }
        return url.toString();
    }
}
