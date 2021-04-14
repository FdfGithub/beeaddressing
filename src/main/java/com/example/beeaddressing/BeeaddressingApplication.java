package com.example.beeaddressing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan(basePackages = {"com.example.beeaddressing.dao"})
@EnableScheduling
public class BeeaddressingApplication{
    public static void main(String[] args) {
        SpringApplication.run(BeeaddressingApplication.class, args);
    }
}
