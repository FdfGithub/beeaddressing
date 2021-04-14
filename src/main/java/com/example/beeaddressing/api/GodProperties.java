package com.example.beeaddressing.api;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "god")
public class GodProperties {
    private String key;

    private String radius;

    private final String firstPage = "1";

    //更换成动态的
    public enum POI {
        Relaxation("休闲娱乐", "080000|110000"),
        Food("餐饮", "050000"),
        Shopping("大型商场","060100|060400"),
        House("住宿服务|商务住宅", "100000|120000"),
        Office("写字楼","120201"),
        School("学校", "140000"),
        Traffic("交通站点", "150700|150000"),
        Convenience("便利店", "060200"),
        ;

        private final String name;
        private final String type;



        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        POI(String name, String type) {
            this.name = name;
            this.type = type;
        }
    }
}
