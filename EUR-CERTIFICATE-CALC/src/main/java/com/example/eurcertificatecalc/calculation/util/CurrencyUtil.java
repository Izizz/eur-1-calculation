package com.example.eurcertificatecalc.calculation.util;

import com.example.eurcertificatecalc.calculation.model.CurrencyRate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CurrencyUtil {

    private static final String API_URL = "https://bank.gov.ua/NBUStatService/v1/statdirectory/exchangenew?json&valcode=EUR";

    public String getCurrencyRate() throws JsonProcessingException {

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(API_URL, String.class);
        String response = responseEntity.getBody();

        ObjectMapper objectMapper = new ObjectMapper();
        CurrencyRate[] currencyRates = objectMapper.readValue(response, CurrencyRate[].class);

        return currencyRates[0].getResult();
    }
}
