package com.airawarehub.backend.service;

import com.airawarehub.backend.entity.City;
import com.airawarehub.backend.payload.CityAirRequestDto;
import com.airawarehub.backend.payload.RankCityResponse;

import java.util.List;

public interface AirService {
    public Object getCountries();

    public Object getStatesByCountry(String countryName);

    Object getCityAirData(CityAirRequestDto cityAirRequestDto);

    List<RankCityResponse> getPollutedCity();

    List<RankCityResponse> getCleanCity();

    public void getAllStates();

    List<City> searchByCityPrefix(String cityName);
}