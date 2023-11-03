package com.airawarehub.backend.payload;

import lombok.Builder;
import lombok.Data;

@Data
//@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class SimplifiedAirQualityDTO {
    private String city;
    private String state;
    private String country;
    private double[] coordinates;
    private String ts;
    private Pollution pollution;
    private Weather weather;
}
