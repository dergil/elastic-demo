package com.github.dergil.elasticdemo.car.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dergil.elasticdemo.car.domain.dto.calculate.CalculateRequest;
import com.github.dergil.elasticdemo.car.domain.dto.calculate.CalculateView;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;


@Service
public class CalculatorApiImpl implements CalculatorApi{

//    queries calculator for calculation
    @Transactional
    public CalculateView queryCalculator(CalculateRequest request) throws JsonProcessingException {
        WebClient client = WebClient.create();
        String uri = String.format("http://calculator:8084/calculate?price=%s&salesTax=%s",
                request.getPrice(), request.getSalesTax());
        WebClient.ResponseSpec responseSpec = client.get().uri(uri).retrieve();
        String jsonResponse = responseSpec.bodyToMono(String.class).block();
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(jsonResponse, CalculateView.class);
    }


}
