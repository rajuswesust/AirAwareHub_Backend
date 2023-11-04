package com.airawarehub.backend.service.impl;

import com.airawarehub.backend.exception.ApiException;
import com.airawarehub.backend.exception.BadRequestException;
import com.airawarehub.backend.exception.ResponseStatusExceptionCustom;
import com.airawarehub.backend.payload.GdpTotalResponse;
import com.airawarehub.backend.payload.SimpleResponse;
import com.airawarehub.backend.repository.CountryRepository;
import com.airawarehub.backend.service.SocialEconomicFactorService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.text.DecimalFormat;
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
    public GdpTotalResponse getTotalGdp(String countryName) {
        try {
            String code = countryRepository.findByName(countryName).getIsoCode();
            String url = baseUrl.append(code).append("/indicators").
                    append("/NY.GDP.MKTP.CD?format=json").toString();

            HttpHeaders headers = new HttpHeaders();

            // Make a GET call to IQAir
            ResponseEntity<JsonNode> response = restTemplate.exchange(url,
                    HttpMethod.GET, new HttpEntity<>(headers), JsonNode.class);
            log.info("Output form API:{}", response.getBody());

            JsonNode data = response.getBody().get(1).get(0);

            double value = Double.parseDouble(String.valueOf(data.get("value")));

            // Format it to remove scientific notation
            DecimalFormat decimalFormat = new DecimalFormat("0.##########"); // Adjust the number of # symbols as needed
            String formattedValue = decimalFormat.format(value);

            GdpTotalResponse gdpTotalResponse = GdpTotalResponse.builder().value(formattedValue).
                    date(data.get("date").textValue()).build();
            return gdpTotalResponse;

        } catch (Exception e) {
            log.error("Something went wrong while getting value from IQAir API", e);
            if (e instanceof HttpClientErrorException)
                handleHttpClientError((HttpClientErrorException) e);
            throw new ResponseStatusExceptionCustom(
                    SimpleResponse.builder().message("Exception while calling endpoint of IQAir API for data").build());
        }
    }

    private void handleHttpClientError(HttpClientErrorException e) {
        HttpStatus statusCode = HttpStatus.resolve(e.getRawStatusCode());
        JsonNode jsonNode = e.getResponseBodyAs(JsonNode.class);

        if (statusCode == HttpStatus.BAD_REQUEST) {
            throw new BadRequestException(SimpleResponse.builder().message(jsonNode.get("data").get("message").textValue()).statusCode(400).build());
        } else {
            throw new ApiException(statusCode, jsonNode.get("message").textValue());
        }
    }
}

