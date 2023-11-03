package com.airawarehub.backend.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CitySearchDto {
    @JsonProperty("city_name")
    String cityName;
}
