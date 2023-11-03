package com.airawarehub.backend.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class Data {

    @JsonProperty("city")
    private String city;


    @JsonProperty("state")
    private String state;

    @JsonProperty("country")
    private String country;

    @JsonProperty("location")
    private Location location;

    @JsonProperty("current")
    private Current current;
}
