package com.airawarehub.backend.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
//@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SimplifiedAirQualityDTO {
    private String city;
    private String state;
    private String country;
    private double[] coordinates;
    private String ts;
    private Pollution pollution;
    private Weather weather;
}
