package com.github.dergil.elasticdemo.car.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.dergil.elasticdemo.car.domain.exception.EntityNotFoundException;
import com.github.dergil.elasticdemo.car.service.CarServiceImpl;
import com.github.dergil.elasticdemo.car.domain.dto.calculate.CalculateRequest;
import com.github.dergil.elasticdemo.car.domain.dto.calculate.CalculateView;
import com.github.dergil.elasticdemo.car.domain.dto.car.CarCalculateView;
import com.github.dergil.elasticdemo.car.domain.dto.car.CarView;
import com.github.dergil.elasticdemo.car.domain.dto.car.EditCarRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/car")
public class CarController {
// CRUD implemented
    @Autowired
    private CarServiceImpl carServiceImpl;


    @PostMapping()
    public CarView create(@RequestBody @Valid EditCarRequest request) {
        return carServiceImpl.create(request);
    }

    @PutMapping("{id}")
    public CarView update(@PathVariable long id, @RequestBody @Valid EditCarRequest request) throws EntityNotFoundException {
        return carServiceImpl.update(id, request);
    }

    @DeleteMapping("{id}")
    public CarView delete(@PathVariable long id) throws EntityNotFoundException {
        return carServiceImpl.delete(id);
    }

    @GetMapping("{id}")
    public CarView get(@PathVariable long id) throws EntityNotFoundException {
        return carServiceImpl.get(id);
    }

    @GetMapping()
    public List<CarView> getAll() {
        return carServiceImpl.getAll();
    }

//    user can request a calculation of the tax rate for a given car object
    @GetMapping("/tax/{id}")
    public CarCalculateView tax(@PathVariable long id, @RequestParam double tax) throws JsonProcessingException, EntityNotFoundException {
        log.info("Calculating tax " + tax + " for nr. " + id);
        CalculateRequest calculateRequest = new CalculateRequest();
        CarView carView = carServiceImpl.get(id);
        int price = carView.getPrice();
        calculateRequest.setPrice(price);
        calculateRequest.setSalesTax(tax);
        CalculateView calculateView = carServiceImpl.tax(calculateRequest);
        CarCalculateView carCalculateView = new CarCalculateView();
        carCalculateView.setCarView(carView);
        carCalculateView.setCalculateView(calculateView);
        log.info("Returning: " + carCalculateView);
        return carCalculateView;
    }
}
