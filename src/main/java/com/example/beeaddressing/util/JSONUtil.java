package com.example.beeaddressing.util;

import com.google.common.collect.Maps;

import java.util.Map;

public class JSONUtil {

    public static Map<String,Object> StringToMap(String json){
        Map<String,Object> map = Maps.newHashMap();
        String[] split = json.split(",");
        for (String str : split) {
            String[] temp = str.split(":");
            map.put(temp[0],temp[1]);
        }
        return map;
    }
}
