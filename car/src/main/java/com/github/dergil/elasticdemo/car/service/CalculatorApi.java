package com.github.dergil.elasticdemo.car.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.dergil.elasticdemo.car.domain.dto.calculate.CalculateRequest;
import com.github.dergil.elasticdemo.car.domain.dto.calculate.CalculateView;

public interface CalculatorApi {
    CalculateView queryCalculator(CalculateRequest request) throws JsonProcessingException;
}
