package com.airawarehub.backend.controller;

import com.airawarehub.backend.payload.GdpTotalResponse;
import com.airawarehub.backend.service.SocialEconomicFactorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class SocialEconomicFactorsController {

    public final SocialEconomicFactorService socialEconomicFactorService;

    @GetMapping("/total-gdp/{countryName}")
    public ResponseEntity<GdpTotalResponse> getTotalGDP(@PathVariable(value = "countryName") String countryName) {
        return new ResponseEntity<>(socialEconomicFactorService.getTotalGdp(countryName), HttpStatus.OK);
    }

    @GetMapping("/total-population/{countryName}")
    public ResponseEntity<GdpTotalResponse> getTotalPopulation(@PathVariable(value = "countryName") String countryName) {
        return new ResponseEntity<>(socialEconomicFactorService.getTotalGdp(countryName), HttpStatus.OK);
    }

}
