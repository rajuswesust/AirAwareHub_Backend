package com.airawarehub.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class SocialEconomicFactorsController {

    @GetMapping("/total-gdp/{countryName}")
    public ResponseEntity<?> getTotalGDP(@PathVariable(value = "countryName") String countryName) {
        return null;
    }


}
