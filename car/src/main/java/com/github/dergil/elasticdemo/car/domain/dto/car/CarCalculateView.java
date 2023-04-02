package com.github.dergil.elasticdemo.car.domain.dto.car;

import com.github.dergil.elasticdemo.car.domain.dto.calculate.CalculateView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CarCalculateView {
    private CarView carView;
    private CalculateView calculateView;
}
