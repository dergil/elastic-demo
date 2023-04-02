package com.github.dergil.elasticdemo.calculator.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalculateView {
    double priceTotal;
    double price;
    double salesTax;
    double taxAmount;
}
