package com.example.beeaddressing.controller;

import com.example.beeaddressing.common.Response;
import com.example.beeaddressing.pojo.County;
import com.example.beeaddressing.service.CountyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/county")
@CrossOrigin
public class CountyController {
    @Autowired
    private CountyService countyService;


    //通过城市id，获取地区信息
    @GetMapping("/{cityId}")
    public Response getCounties(@PathVariable String cityId){
        List<County> counties = countyService.getCounty(cityId);
        return Response.createSuccess().setData(counties);
    }


}
