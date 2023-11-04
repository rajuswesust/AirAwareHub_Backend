package com.airawarehub.backend.service;

import com.airawarehub.backend.entity.City;
import com.airawarehub.backend.payload.CityAirRequestDto;
import com.airawarehub.backend.payload.RankCityResponse;
import com.airawarehub.backend.payload.SimplifiedAirQualityDTO;

import java.util.List;

public interface AirService {
    public Object getCountries();

    public Object getStatesByCountry(String countryName);

    Object getCityAirData(String city, String state, String country);

    List<RankCityResponse> getPollutedCity();

    List<RankCityResponse> getCleanCity();

    public void getAllStates();

    //List<City> searchByCityPrefix(String cityName);

    List<City> searchByCity(String name);

    List<SimplifiedAirQualityDTO> getAllAvailableCity();
}