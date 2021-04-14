package com.example.beeaddressing.pojo;



import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@Table(name = "bee_village")
public class Village {
    @Id
    private Integer villageId;
    private String villageName;
    private String countyId;
    private Long population;
    private String location;
    private LocalDateTime timestamp;

}
