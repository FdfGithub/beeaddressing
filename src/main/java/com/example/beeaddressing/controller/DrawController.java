package com.example.beeaddressing.controller;


import com.example.beeaddressing.common.Response;
import com.example.beeaddressing.service.DrawService;
import com.example.beeaddressing.vo.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/draw")
@CrossOrigin
public class DrawController {

    @Autowired
    private DrawService drawService;


    @GetMapping("/population/{storeId}")
    public Response population(@PathVariable Long storeId){
        return Response.createSuccess().setData(drawService.population(storeId));
    }

    //指标2：分析距离，住宿、居民区、酒店到选址点的距离
    //L1：300米   L2：500米
    //根据效用分段函数分析距离指标
    //地图：展现各个住宿点和选址点的路径（标出路程和步行时长等数据）
    //第一步：画出附近500米范围内的所有住宅区
    //第二步：连接住宅区和选址点
    //第三步：显示路程和步行时间
//    @GetMapping("/distance/{storeId}")
//    public Response distance(@PathVariable Long storeId){
//        return Response.createSuccess().setData(drawService.distance(storeId));
//    }



    //指标3：周边环境：饼图
    @GetMapping("/surrounds/{storeId}")
    public Response surrounds(@PathVariable Long storeId){
        return Response.createSuccess().setData(drawService.surrounds(storeId));
    }

    //指标4：消费能力：折线图（各年龄结构的消费能力）
//    public Response consume(Long storeId){
//        return null;
//    }


    //指标5：房价   平均值和租金的对比
    @GetMapping("/housePrice/{storeId}")
    public Response housePrice(@PathVariable Long storeId){
        return Response.createSuccess().setData(drawService.housePrice(storeId));
    }
    //玫瑰图：各项指标

    //  /leidatu/id
    @GetMapping("/leidatu/{storeId}")
    public Response leidatu(@PathVariable Long storeId){
        return Response.createSuccess().setData(drawService.leidatu(storeId));
    }
}
