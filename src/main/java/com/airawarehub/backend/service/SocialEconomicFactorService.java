package com.airawarehub.backend.service;

import com.airawarehub.backend.payload.GdpTotalResponse;

public interface SocialEconomicFactorService {

    public GdpTotalResponse getTotalGdp(String countryName);
}
