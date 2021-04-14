package com.example.beeaddressing.dao;

import com.example.beeaddressing.pojo.Village;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface VillageMapper extends Mapper<Village> {


    List<Village> findVillages();
}
