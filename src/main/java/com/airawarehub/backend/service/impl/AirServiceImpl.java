package com.airawarehub.backend.service.impl;

import com.airawarehub.backend.entity.*;
import com.airawarehub.backend.exception.ApiException;
import com.airawarehub.backend.exception.BadRequestException;
import com.airawarehub.backend.exception.ResponseStatusExceptionCustom;
import com.airawarehub.backend.payload.*;
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
import org.springframework.web.client.HttpClientErrorException;
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
    private final CityAirDataRepository cityAirDataRepository;

    @Value("${iqair.api.base.url}")
    private StringBuilder url;

    @Value("${iqair.api.key}")
    private String apiKey;


    @Override
    public Object getCountries() {
        try {
            String urlCountries = url.append("countries?key=").append(apiKey).toString();
            HttpHeaders headers = new HttpHeaders();

            // Make a GET call to IQAir
            ResponseEntity<JsonNode> response = restTemplate.exchange(urlCountries,
                    HttpMethod.GET, new HttpEntity<>(headers), JsonNode.class);
            log.info("Output form API:{}", response.getBody());
            saveCountries(Objects.requireNonNull(response.getBody()));
            return response.getBody();
        } catch (Exception e) {
            log.error("Something went wrong while getting value from IQAir API", e);
            if (e instanceof HttpClientErrorException)
                handleHttpClientError((HttpClientErrorException) e);
            throw new ResponseStatusExceptionCustom(
                    SimpleResponse.builder().message("Exception while calling endpoint of IQAir API for data").build());
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
        } catch (Exception e) {
            log.error("Something went wrong while getting value from IQAir API", e);
            if (e instanceof HttpClientErrorException)
                handleHttpClientError((HttpClientErrorException) e);
            throw new ResponseStatusExceptionCustom(
                    SimpleResponse.builder().message("Exception while calling endpoint of IQAir API for data").build());
        }
    }

    @Override
    public Object getCityAirData(String city, String state, String country) {
        if(cityAirDataRepository.existsByCity(city)) {
            return convert(cityAirDataRepository.findByCity(city));
        }
        try {
            String urlCity = url.append("city?").append("city=").append(city).
                    append("&state=").append(state).append("&country=").append(country).append("&key=").append(apiKey).toString();
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
            saveCityAirData(simplifiedAirQualityDTO);
            return simplifiedAirQualityDTO;
        } catch (Exception e) {
            log.error("Something went wrong while getting value from IQAir API", e);
            if (e instanceof HttpClientErrorException)
                handleHttpClientError((HttpClientErrorException) e);
            throw new ResponseStatusExceptionCustom(
                    SimpleResponse.builder().message("Exception while calling endpoint of IQAir API for data").
                            statusCode(500).build());
        }
    }

    private void saveCityAirData(SimplifiedAirQualityDTO simplifiedAirQualityDTO) {
        String lat = String.valueOf(simplifiedAirQualityDTO.getCoordinates()[0]);
        String lng = String.valueOf(simplifiedAirQualityDTO.getCoordinates()[1]);
       CityAirData cityAirData  = CityAirData.builder().city(simplifiedAirQualityDTO.getCity()).country(simplifiedAirQualityDTO.getCountry()).state(simplifiedAirQualityDTO.getState()).
                lat(lat).lng(lng).mainus(simplifiedAirQualityDTO.getPollution().getMainus()).maincn(simplifiedAirQualityDTO.getPollution().getMaincn()).
                aqius(simplifiedAirQualityDTO.getPollution().getAqius()).aqicn(simplifiedAirQualityDTO.getPollution().getAqicn()).tp(simplifiedAirQualityDTO.getWeather().getTp()).wd(simplifiedAirQualityDTO.getWeather().getWd()).ws(simplifiedAirQualityDTO.getWeather().getWs()).ic(simplifiedAirQualityDTO.getWeather().getIc()).
                pr(simplifiedAirQualityDTO.getWeather().getPr()).hu(simplifiedAirQualityDTO.getWeather().getHu()).ts(simplifiedAirQualityDTO.getTs()).build();
        cityAirDataRepository.save(cityAirData);
    }
    private SimplifiedAirQualityDTO convert(CityAirData cityAirData) {
        SimplifiedAirQualityDTO simplifiedAirQualityDTO = new SimplifiedAirQualityDTO();
        simplifiedAirQualityDTO.setCity(cityAirData.getCity());
        simplifiedAirQualityDTO.setCountry(cityAirData.getCountry());
        simplifiedAirQualityDTO.setState(cityAirData.getState());

        double lat = Double.parseDouble(cityAirData.getLat());
        double lng = Double.parseDouble(cityAirData.getLng());
        simplifiedAirQualityDTO.setCoordinates(new double[]{lat, lng});

        Pollution pollution = new Pollution();
        pollution.setMainus(cityAirData.getMainus());
        pollution.setMaincn(cityAirData.getMaincn());
        pollution.setAqius(cityAirData.getAqius());
        pollution.setAqicn(cityAirData.getAqicn());

        Weather weather = new Weather();
        weather.setTp(cityAirData.getTp());
        weather.setWd(cityAirData.getWd());
        weather.setWs(cityAirData.getWs());
        weather.setIc(cityAirData.getIc());
        weather.setPr(cityAirData.getPr());
        weather.setHu(cityAirData.getHu());

        simplifiedAirQualityDTO.setPollution(pollution);
        simplifiedAirQualityDTO.setWeather(weather);
        simplifiedAirQualityDTO.setTs(cityAirData.getTs());

        return simplifiedAirQualityDTO;
    }

    @Override
    public List<RankCityResponse> getPollutedCity() {
        try{
            List<PollutedCity> pollutedCities = pollutedCityRepository.findAll();
            List<RankCityResponse> pollutedCitiesList = new ArrayList<>();

            for (PollutedCity it : pollutedCities) {

                //StringBuilder temporaryUrl = new StringBuilder(url);
                //String urlCity = temporaryUrl.append("city?").append("city=").append(it.getName()).
                //        append("&state=").append(it.getState()).append("&country=").append(it.getCountry()).append("&key=").append(apiKey).toString();
                //HttpHeaders headers = new HttpHeaders();

                //ResponseEntity<JsonNode> response = restTemplate.exchange(urlCity,
                //        HttpMethod.GET, new HttpEntity<>(headers), JsonNode.class);

                //int aqius = response.getBody().get("data").get("current").get("pollution").get("aqius").asInt();
                //int aqicn = response.getBody().get("data").get("current").get("pollution").get("aqicn").asInt();
                //pollutedCitiesList.add(RankCityResponse.builder().cityName(it.getName()).aqius(aqius).aqicn(aqicn).build());
                pollutedCitiesList.add(RankCityResponse.builder().cityName(it.getName()).aqius(it.getAqius()).aqicn(it.getAqicn()).build());
                //System.out.println(it.getName() + ": " + aqius + ", " + aqicn);
            }
            return pollutedCitiesList;
        } catch (Exception e) {
            log.error("Something went wrong while getting value from IQAir API", e);
            if (e instanceof HttpClientErrorException)
                handleHttpClientError((HttpClientErrorException) e);
            throw new ResponseStatusExceptionCustom(
                    SimpleResponse.builder().message("Exception while calling endpoint of IQAir API for data").build());
        }
    }



    @Override
    public List<RankCityResponse> getCleanCity() {
        try{
            List<CleanCity> pollutedCities = cleanCityRepository.findAll();
            List<RankCityResponse> cleanCitiesList = new ArrayList<>();
            for (CleanCity it : pollutedCities) {
//                StringBuilder temporaryUrl = new StringBuilder(url);
//                String urlCity = temporaryUrl.append("city?").append("city=").append(it.getName()).
//                        append("&state=").append(it.getState()).append("&country=").append(it.getCountry()).append("&key=").append(apiKey).toString();
//                HttpHeaders headers = new HttpHeaders();
//
//                ResponseEntity<JsonNode> response = restTemplate.exchange(urlCity,
//                        HttpMethod.GET, new HttpEntity<>(headers), JsonNode.class);

                //int aqius = response.getBody().get("data").get("current").get("pollution").get("aqius").asInt();
                //int aqicn = response.getBody().get("data").get("current").get("pollution").get("aqicn").asInt();
                //it.setAqicn(aqicn);
                //it.setAqius(aqius);
                cleanCitiesList.add(RankCityResponse.builder().cityName(it.getName()).aqius(it.getAqius()).aqicn(it.getAqicn()).build());
                //System.out.println(it.getName() + ": " + aqius + ", " + aqicn);
            }
            return cleanCitiesList;
        } catch (Exception e) {
            log.error("Something went wrong while getting value from IQAir API", e);
            if (e instanceof HttpClientErrorException)
                handleHttpClientError((HttpClientErrorException) e);
            throw new ResponseStatusExceptionCustom(
                    SimpleResponse.builder().message("Exception while calling endpoint of IQAir API for data").build());
        }
    }

    public void getAllStates() {
        List<Country> countries = countryRepository.findAll();
        int cnt = 0;
        for (Country country : countries) {
            getStatesByCountry(country.getName());
            cnt++;
            if (cnt == 50) break;
        }
    }

    @Override
    public List<City> searchByCity(String cityName) {
        return cityRepository.findCitiesByNameContaining(cityName);
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
        //countryRepository.saveAll(countires);
    }


    private Object handleHttpClientError(HttpClientErrorException e) {
        HttpStatus statusCode = HttpStatus.resolve(e.getRawStatusCode());
        JsonNode jsonNode = e.getResponseBodyAs(JsonNode.class);

        if (statusCode == HttpStatus.BAD_REQUEST) {
            throw new BadRequestException(SimpleResponse.builder().message(jsonNode.get("data").get("message").textValue()).statusCode(400).build());
        } else {
            throw new ApiException(statusCode, jsonNode.get("message").textValue());
        }
    }

}
