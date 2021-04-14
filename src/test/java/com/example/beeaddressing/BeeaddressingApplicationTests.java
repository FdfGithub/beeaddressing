package com.example.beeaddressing;


import com.example.beeaddressing.dao.StoreMapper;
import com.example.beeaddressing.model.ScoreModel;
import com.example.beeaddressing.util.ReadExcel;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mysql.cj.jdbc.Driver;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.List;
import java.util.Map;


@SpringBootTest
@Slf4j
class BeeaddressingApplicationTests {

    @Autowired
    private ScoreModel scoreModel;

    @Test
    void contextLoads() throws InterruptedException {
//        scoreModel.addLocation();
//        scoreModel.execute();
//        scoreModel.addLocationVillage();
    }

    @Test
    void setScoreModel() throws InterruptedException {
//        scoreModel.execute();
    }

    @Test
    void setScoreModel2() throws InterruptedException {
//        scoreModel.execute();
    }


    @Autowired
    private StoreMapper storeMapper;

    @Test
    void test() {
//        ReadExcel obj = new ReadExcel();
//        File file = new File("D:/citycode.xls");
//        List excelList = obj.readExcel(file);
//        for (int i = 2; i < excelList.size(); i++) {
//            List list = (List) excelList.get(i);
//            int result = storeMapper.updateAdcode("%" + String.valueOf(list.get(0)).replace("区","").replace("县","") + "%", String.valueOf(list.get(1)));
//            log.info("cityName:{},adcode:{},result:{}", "%" + String.valueOf(list.get(0)).replace("区","").replace("县","") + "%", String.valueOf(list.get(1)),result);
//        }
    }
}
