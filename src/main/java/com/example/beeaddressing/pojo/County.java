package com.example.beeaddressing.pojo;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Table(name = "bee_county")
public class County {
    @Id
    private String countyId;
    private String cityId;
    private String countyName;
    private String adcode;
    private LocalDateTime timestamp;
}
