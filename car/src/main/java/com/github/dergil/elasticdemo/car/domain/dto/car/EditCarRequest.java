package com.github.dergil.elasticdemo.car.domain.dto.car;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class EditCarRequest {

    @NotBlank
    private String name;
    @NotNull
    private Double price;
    private float milesPerGallon;
    private int cylinders;
    private int displacement;
    private int horsepower;
    private int weightInPounds;
    private float acceleration;
    private Date year;
    private String origin;
}
