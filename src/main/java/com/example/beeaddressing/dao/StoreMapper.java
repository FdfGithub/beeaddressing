package com.example.beeaddressing.dao;

import com.example.beeaddressing.pojo.Store;
import com.example.beeaddressing.vo.Power;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;


public interface StoreMapper extends Mapper<Store>{
    List<Store> findStores(@Param("cityId") String cityId,@Param("countyId") String countyId);

    List<Map<String,Object>> findPowers(@Param("cityId") String cityId,@Param("countyId") String countyId);

    int updateStores(@Param("scores") List<Map<String,Object>> scores);

    int updateStoresBatch(@Param("stores") List<Store> stores);

    //查找该选址点所在地区的所有选址点
    List<Store> findStoresByStoreId(Long storeId);

    //根据countyName模糊匹配cityName，就把adcode加入到store
    int updateAdcode(@Param("cityName") String cityName,@Param("adcode") String adcode);

    List<Store> findStoresByCountyId(String countyId);
}
