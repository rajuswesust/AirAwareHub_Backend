package com.airawarehub.backend.controller;

import com.airawarehub.backend.payload.CityAirRequestDto;
import com.airawarehub.backend.payload.CitySearchDto;
import com.airawarehub.backend.payload.RankCityResponse;
import com.airawarehub.backend.service.AirService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AirDetailsController {

    private final AirService airService;

    @GetMapping("/countries")
    public ResponseEntity<?> getCountries() {
        return new ResponseEntity<>(airService.getCountries(), HttpStatus.OK);
    }

    @GetMapping("/states")
    public ResponseEntity<?> getStates(@RequestParam(value = "country", required = false) String countryName) {
        if(countryName.equals("")) {
            throw new IllegalArgumentException("Missing required parameter `country`");
        }
        return new ResponseEntity<>(airService.getStatesByCountry(countryName), HttpStatus.OK);
    }

    @GetMapping("/city")
    public ResponseEntity<?> getCityAirData(@RequestBody CityAirRequestDto cityAirRequestDto) {
        return new ResponseEntity<>(airService.getCityAirData(cityAirRequestDto), HttpStatus.OK);
    }

    @GetMapping("/top-10-polluted-city")
    public ResponseEntity<List<RankCityResponse>> getPollutedCity() {
        return new ResponseEntity<>(airService.getPollutedCity(), HttpStatus.OK);
    }

    @GetMapping("/top-10-clean-city")
    public ResponseEntity<List<RankCityResponse>> getCleanCity() {
        return new ResponseEntity<>(airService.getCleanCity(), HttpStatus.OK);
    }

    @GetMapping("/search-city")
    public ResponseEntity<?> searchByCity(@RequestParam(value = "city_name") String cityName) {
        return  new ResponseEntity<>(airService.searchByCity(cityName.toLowerCase()), HttpStatus.OK);
    }
    @GetMapping("/all-states")
    public ResponseEntity<?> getAllState() {
        airService.getAllStates();
        return new ResponseEntity<>("", HttpStatus.OK);
    }
}