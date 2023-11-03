package com.airawarehub.backend.payload;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class Weather {
//    private int pressure;
//    private int humidity;
//    private double wind_speed;
//    private int wind_direction;
//    private String icon_code;
    @JsonIgnore
    private String ts;
    private String tp;
    private String pr;
    private int hu;
    private double ws;
    private int wd;
    private String ic;
}
