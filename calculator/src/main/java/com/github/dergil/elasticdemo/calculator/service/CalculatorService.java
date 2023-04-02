package com.github.dergil.elasticdemo.calculator.service;


import com.github.dergil.elasticdemo.calculator.domain.dto.CalculateView;
import com.github.dergil.elasticdemo.calculator.domain.dto.CalculateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CalculatorService {
    public CalculateView calculate(CalculateRequest request){
        double price = request.getPrice();
        double salesTax = request.getSalesTax();
        double taxAmount = price * salesTax;

        CalculateView calculateView = new CalculateView();

        calculateView.setPriceTotal(price + taxAmount);
        calculateView.setPrice(price);
        calculateView.setSalesTax(salesTax);
        calculateView.setTaxAmount(taxAmount);
        log.info("Response: " + calculateView);
        return calculateView;
    }
}
