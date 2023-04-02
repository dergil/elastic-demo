package com.github.dergil.elasticdemo.car.domain.mapper;

import com.github.dergil.elasticdemo.car.domain.model.Car;
import com.github.dergil.elasticdemo.car.domain.dto.car.CarView;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CarViewMapper {
//    Maps the car entity to the CarView DTO
    CarView toCarView(Car car);
}
