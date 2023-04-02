package com.github.dergil.elasticdemo.car.domain.dto.car;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CarView {

    private long id;

    private String name;
    private int price;
    private int milesPerGallon;
    private int cylinders;
    private int displacement;
    private int horsepower;
    private int weightInPounds;
    private int acceleration;
    private LocalDate year;
    private String origin;
}
