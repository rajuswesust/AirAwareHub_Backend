package com.airawarehub.backend.service.impl;

import com.airawarehub.backend.entity.*;
import com.airawarehub.backend.payload.AirQualityDto;
import com.airawarehub.backend.payload.CityAirRequestDto;
import com.airawarehub.backend.payload.RankCityResponse;
import com.airawarehub.backend.payload.SimplifiedAirQualityDTO;
import com.airawarehub.backend.repository.*;
import com.airawarehub.backend.service.AirService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
@PropertySources({
        @PropertySource("classpath:application.properties"),
        @PropertySource("classpath:external-api.properties")
})
public class AirServiceImpl implements AirService {


    private final RestTemplate restTemplate;
    private final CountryRepository countryRepository;
    private final StateRepository stateRepository;
    private final PollutedCityRepository pollutedCityRepository;
    private final CleanCityRepository cleanCityRepository;
    private final CityRepository cityRepository;

    @Value("${iqair.api.base.url}")
    private StringBuilder url;

    @Value("${iqair.api.key}")
    private String apiKey;



    @Override
    public Object getCountries() {
        try{
            String urlCountries = url.append("countries?key=").append(apiKey).toString();
            HttpHeaders headers = new HttpHeaders();

            // Make a GET call to IQAir
            ResponseEntity<JsonNode> response = restTemplate.exchange(urlCountries,
                    HttpMethod.GET, new HttpEntity<>(headers), JsonNode.class);
            log.info("Output form API:{}", response.getBody());
            saveCountries(Objects.requireNonNull(response.getBody()));
            return response.getBody();
        }
        catch (Exception e) {
            log.error("Something went wrong while getting value from IQAir API", e);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Exception while calling endpoint of IQAir API for data", e);
        }
    }

    @Override
    public Object getStatesByCountry(String countryName) {
        try {
            String urlState = url.append("states?").append("country=").append(countryName).
                    append("&key=").append(apiKey).toString();
            HttpHeaders headers = new HttpHeaders();

            // Make a GET call to IQAir
            ResponseEntity<JsonNode> response = restTemplate.exchange(urlState,
                    HttpMethod.GET, new HttpEntity<>(headers), JsonNode.class);
            log.info("Output form API:{}", response.getBody());
            saveStates(Objects.requireNonNull(response.getBody()), countryName);
            return response.getBody();
        }
        catch (Exception e) {
            log.error("Something went wrong while getting value from IQAir API", e);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Exception while calling endpoint of IQAir API for data", e);
        }
    }

    @Override
    public Object getCityAirData(CityAirRequestDto cityAirRequestDto) {
        try {
            String urlCity = url.append("city?").append("city=").append(cityAirRequestDto.getCity()).
                    append("&state=").append(cityAirRequestDto.getState()).append("&country=").append(cityAirRequestDto.getCountry()).append("&key=").append(apiKey).toString();
            HttpHeaders headers = new HttpHeaders();

            // Make a GET call to IQAir
            ResponseEntity<String> response = restTemplate.exchange(urlCity,
                    HttpMethod.GET, new HttpEntity<>(headers), String.class);
            log.info("Output form API:{}", response.getBody());

            String responseBody = response.getBody();
            ObjectMapper objectMapper = new ObjectMapper();
           AirQualityDto airQualityDto = objectMapper.readValue(responseBody, AirQualityDto.class);

            SimplifiedAirQualityDTO simplifiedAirQualityDTO = SimplifiedAirQualityDTO.builder().
                    city(airQualityDto.getData().getCity()).
                    state(airQualityDto.getData().getState()).
                    country(airQualityDto.getData().getCountry()).
                    coordinates(airQualityDto.getData().getLocation().getCoordinates()).
                    ts(LocalDateTime.now().toString()).
                    weather(airQualityDto.getData().getCurrent().getWeather()).
                    pollution(airQualityDto.getData().getCurrent().getPollution()).
                    build();

            return simplifiedAirQualityDTO;
        }
        catch (Exception e) {
            log.error("Something went wrong while getting value from IQAir API", e);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Exception while calling endpoint of IQAir API for data", e);
        }
    }

    @Override
    public List<RankCityResponse> getPollutedCity() {
        List<PollutedCity> pollutedCities = pollutedCityRepository.findAll();
        List<RankCityResponse> pollutedCitiesList = new ArrayList<>();
        for(PollutedCity it: pollutedCities) {
            StringBuilder temporaryUrl = new StringBuilder(url);
            String urlCity = temporaryUrl.append("city?").append("city=").append(it.getName()).
                    append("&state=").append(it.getState()).append("&country=").append(it.getCountry()).append("&key=").append(apiKey).toString();
            HttpHeaders headers = new HttpHeaders();

            ResponseEntity<JsonNode> response = restTemplate.exchange(urlCity,
                    HttpMethod.GET, new HttpEntity<>(headers), JsonNode.class);

            int aqius = response.getBody().get("data").get("current").get("pollution").get("aqius").asInt();
            int aqicn = response.getBody().get("data").get("current").get("pollution").get("aqicn").asInt();
            pollutedCitiesList.add(RankCityResponse.builder().cityName(it.getName()).aqius(aqius).aqicn(aqicn).build());
            System.out.println(it.getName() + ": " + aqius + ", " + aqicn);
        }
        return pollutedCitiesList;
    }

    @Override
    public List<RankCityResponse> getCleanCity() {
        List<CleanCity> pollutedCities = cleanCityRepository.findAll();
        List<RankCityResponse> cleanCitiesList = new ArrayList<>();
        for(CleanCity it: pollutedCities) {
            StringBuilder temporaryUrl = new StringBuilder(url);
            String urlCity = temporaryUrl.append("city?").append("city=").append(it.getName()).
                    append("&state=").append(it.getState()).append("&country=").append(it.getCountry()).append("&key=").append(apiKey).toString();
            HttpHeaders headers = new HttpHeaders();

            ResponseEntity<JsonNode> response = restTemplate.exchange(urlCity,
                    HttpMethod.GET, new HttpEntity<>(headers), JsonNode.class);

            int aqius = response.getBody().get("data").get("current").get("pollution").get("aqius").asInt();
            int aqicn = response.getBody().get("data").get("current").get("pollution").get("aqicn").asInt();
            cleanCitiesList.add(RankCityResponse.builder().cityName(it.getName()).aqius(aqius).aqicn(aqicn).build());
            System.out.println(it.getName() + ": " + aqius + ", " + aqicn);
        }
        return cleanCitiesList;
    }

    public void getAllStates() {
        List<Country> countries = countryRepository.findAll();
        int cnt = 0;
        for(Country country: countries) {
           getStatesByCountry(country.getName());
           cnt++;
           if(cnt == 50) break;
        }
    }

    @Override
    public List<City> searchByCityPrefix(String cityName) {
        return cityRepository.findByNameStartingWith(cityName);
    }

    private void saveStates(JsonNode responseBody, String countryName) {
        for (JsonNode stateNode : responseBody.get("data")) {
            System.out.println(stateNode);
            String stateName = stateNode.get("state").textValue();
            State state = new State();
            state.setName(stateName);
            state.setCountryName(countryName);
            stateRepository.save(state);
        }
    }

    public void saveCountries(JsonNode responseBody) {
        List<Country> countires = new ArrayList<>();
        for (JsonNode countryNode : responseBody.get("data")) {
            //System.out.println(countryNode);
            String countryName = countryNode.get("country").textValue();
            System.out.println("#" + countryName);
            countires.add(Country.builder().name(countryName).build());
        }
        System.out.println("size of list: " + countires.size());
       countryRepository.saveAll(countires);
    }


}
