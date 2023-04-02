package com.github.dergil.elasticdemo.car.domain.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;

@Entity
@Data
public class Car {
//    Automatic ID creation, beginning at 1
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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
