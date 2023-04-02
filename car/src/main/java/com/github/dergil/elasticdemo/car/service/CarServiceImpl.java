package com.github.dergil.elasticdemo.car.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.dergil.elasticdemo.car.domain.exception.EntityNotFoundException;
import com.github.dergil.elasticdemo.car.domain.model.Car;
import com.github.dergil.elasticdemo.car.domain.dto.calculate.CalculateRequest;
import com.github.dergil.elasticdemo.car.domain.dto.calculate.CalculateView;
import com.github.dergil.elasticdemo.car.domain.dto.car.CarView;
import com.github.dergil.elasticdemo.car.domain.dto.car.EditCarRequest;
import com.github.dergil.elasticdemo.car.domain.mapper.CarEditMapper;
import com.github.dergil.elasticdemo.car.domain.mapper.CarViewMapper;
import com.github.dergil.elasticdemo.car.repository.CarRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class CarServiceImpl implements CarService {
//    Mappers get created at runtime
    @Autowired
    private CarEditMapper carEditMapper;
    @Autowired
    private CarViewMapper carViewMapper;
    @Autowired
    private CarRepository carRepository;
    @Autowired
    private CalculatorApiImpl calculatorService;

    @Transactional
    public CarView create(EditCarRequest request) {
        log.info("Received EditCarRequest: " + request);
        Car car = carEditMapper.create(request);
        carRepository.save(car);
        CarView carView = carViewMapper.toCarView(car);
        log.info("Returning: " + carView);
        return carView;
    }

    @Transactional
    public CarView update(long id, EditCarRequest request) throws EntityNotFoundException {
        log.info("Received EditCarRequest: " + request);
        Car car = carRepository.getCarById(id);
        carEditMapper.update(request, car);
        car = carRepository.save(car);
        CarView carView = carViewMapper.toCarView(car);
        log.info("Returning: " + carView);
        return carView;
    }

    @Transactional
    public CarView delete(long id) throws EntityNotFoundException {
        log.info("Deleting nr. " + id);
        Car car = carRepository.getCarById(id);
        carRepository.delete(car);
        CarView carView = carViewMapper.toCarView(car);
        log.info("Returning: " + carView);
        return carView;
    }

    @Transactional
    public CarView get(long id) throws EntityNotFoundException {
        log.info("Reading nr. " + id);
        Car car = carRepository.getCarById(id);
        CarView carView = carViewMapper.toCarView(car);
        log.info("Returning: " + carView);
        return carView;
    }

    @Transactional
    public List<CarView> getAll() {
        log.info("Reading all");
        List<Car> cars = carRepository.findAll();
        List<CarView> carViews = new ArrayList<>();
        for (Car car : cars) {
            carViews.add(carViewMapper.toCarView(car));
        }
        log.info("Returning: " + carViews);
        return carViews;
    }

    @Transactional
    public CalculateView tax(CalculateRequest request) throws JsonProcessingException {
        return calculatorService.queryCalculator(request);
    }
}
