package com.airawarehub.backend.payload;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GdpTotalResponse {
    public Double value;
    public String date;
}
