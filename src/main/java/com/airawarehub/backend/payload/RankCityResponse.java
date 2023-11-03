package com.airawarehub.backend.payload;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class RankCityResponse {
    private String cityName;
    private int aqius;
    private int aqicn;
}
