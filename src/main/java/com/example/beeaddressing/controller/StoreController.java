package com.example.beeaddressing.controller;

import com.example.beeaddressing.common.Response;
import com.example.beeaddressing.pojo.Store;
import com.example.beeaddressing.service.StoreService;
import com.example.beeaddressing.vo.Power;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/store")
@CrossOrigin
public class StoreController {
    @Autowired
    private StoreService storeService;

    //查找选址点
    @GetMapping("/{cityId}/{countyId}/{page}/{size}")
    public Response findPoints(@PathVariable String cityId, @PathVariable String countyId
            , @PathVariable int page, @PathVariable int size) {
        List<Store> stores = storeService.findPage(cityId, countyId, page, size);
        return Response.createSuccess().setData(stores);
    }

    @GetMapping("/{cityId}/{page}/{size}")
    public Response findPoints(@PathVariable String cityId, @PathVariable int page, @PathVariable int size) {
        List<Store> stores = storeService.findPage(cityId, null, page, size);
        return Response.createSuccess().setData(stores);
    }


    @GetMapping("/{cityId}/{countyId}/powers")
    public Response findPowers(@PathVariable String cityId, @PathVariable String countyId){
        List<Power> powers = storeService.findPowers(cityId, countyId);
        return Response.createSuccess().setData(powers);
    }

    @GetMapping("/{cityId}/powers")
    public Response findPowers(@PathVariable String cityId){
        List<Power> powers = storeService.findPowers(cityId,null);
        return Response.createSuccess().setData(powers);
    }
}
