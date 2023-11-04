package com.airawarehub.backend.service.impl;

import com.airawarehub.backend.repository.CountryRepository;
import com.airawarehub.backend.service.SocialEconomicFactorService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
@PropertySources({
        @PropertySource("classpath:application.properties"),
        @PropertySource("classpath:external-api.properties")
})
public class SocialEconomicFactorServiceImpl implements SocialEconomicFactorService {


    private final CountryRepository countryRepository;
    private final RestTemplate restTemplate;

    @Value("${api.world.bank.url}")
    private StringBuilder baseUrl;

    //https://api.worldbank.org/v2/countries/pk/indicators/NY.GDP.MKTP.CD?format=json
    @Override
    public Object getTotalGdp(String countryName) {
        try{
            String code = countryRepository.findByName(countryName).getIsoCode();
            String url = baseUrl.append(code).append("/indicators").
                    append("/NY.GDP.MKTP.CD?format=json").toString();

            HttpHeaders headers = new HttpHeaders();

            // Make a GET call to IQAir
            ResponseEntity<JsonNode> response = restTemplate.exchange(url,
                    HttpMethod.GET, new HttpEntity<>(headers), JsonNode.class);
            log.info("Output form API:{}", response.getBody());

            JsonNode data = response.getBody().get(1).get(1);
            data.get("value").textValue();
            data.get("date").textValue();
            return response.getBody();

        } catch (Exception e) {

        }
        return null;
    }
}
