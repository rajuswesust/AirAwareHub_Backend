package com.airawarehub.backend.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AirQualityDto {

    @JsonProperty("status")
    private String status;

    @JsonProperty("data")
    private Data data;
}
