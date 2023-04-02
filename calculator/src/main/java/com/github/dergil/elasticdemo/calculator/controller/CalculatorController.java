package com.github.dergil.elasticdemo.calculator.controller;


import com.github.dergil.elasticdemo.calculator.domain.dto.CalculateView;
import com.github.dergil.elasticdemo.calculator.domain.dto.CalculateRequest;
import com.github.dergil.elasticdemo.calculator.service.CalculatorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class CalculatorController {

    @Autowired
    private CalculatorService calculatorService;

    @GetMapping("/calculate")
    public CalculateView calculate(
            @RequestParam(required = true)  double price,
            @RequestParam(required = true)  double salesTax
            ){
        CalculateRequest request = new CalculateRequest();
        request.setPrice(price);
        request.setSalesTax(salesTax);
        log.info("Received request: " + request);
        return calculatorService.calculate(request);
    }
}
