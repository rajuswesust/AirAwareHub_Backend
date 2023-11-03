package com.airawarehub.backend.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class Current {

    @JsonProperty("pollution")
    private Pollution pollution;

    @JsonProperty("weather")
    private Weather weather;
}
