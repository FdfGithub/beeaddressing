package com.example.beeaddressing.pojo;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@Table(name = "bee_city")
public class City {
    @Id
    private String cityId;
    private String cityName;
    private LocalDateTime timestamp;
}
