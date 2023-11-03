package com.airawarehub.backend.payload;

import lombok.Data;

@Data
public class CityAirRequestDto {
    private String city;
    private String state;
    private String country;
}
