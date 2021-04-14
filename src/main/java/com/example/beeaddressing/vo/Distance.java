package com.example.beeaddressing.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Distance {
    private List<Map<String,Object>> houses;
    private Integer maxDistance;
    private Integer minDistance;
}
