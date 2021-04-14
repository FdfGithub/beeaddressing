package com.example.beeaddressing.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Surrounds {
    private List<Map<String,Object>> surrounds;
    private String location;
}
