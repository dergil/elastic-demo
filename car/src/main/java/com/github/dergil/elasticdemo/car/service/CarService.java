package com.github.dergil.elasticdemo.car.service;

import com.github.dergil.elasticdemo.car.domain.dto.car.CarView;
import com.github.dergil.elasticdemo.car.domain.dto.car.EditCarRequest;
import com.github.dergil.elasticdemo.car.domain.exception.EntityNotFoundException;

import java.util.List;

public interface CarService {
    CarView create(EditCarRequest request);

    CarView update(long id, EditCarRequest request) throws EntityNotFoundException;

    CarView delete(long id) throws EntityNotFoundException;

    CarView get(long id) throws EntityNotFoundException;

    List<CarView> getAll();
}
