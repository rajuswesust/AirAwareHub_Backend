package com.airawarehub.backend.payload;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
public class Pollution {
    @JsonIgnore
    private String ts;
    private int aqius;
    private String mainus;
    private int aqicn;
    private String maincn;
}
