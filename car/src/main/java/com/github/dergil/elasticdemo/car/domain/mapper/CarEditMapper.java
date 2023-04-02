package com.github.dergil.elasticdemo.car.domain.mapper;

import com.github.dergil.elasticdemo.car.domain.dto.car.EditCarRequest;
import com.github.dergil.elasticdemo.car.domain.model.Car;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import static org.mapstruct.NullValueCheckStrategy.ALWAYS;
import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring")
public interface CarEditMapper {
//    Maps the EditCarRequest DTO to the Car entity
    Car create(EditCarRequest request);

    //    Updates the Car entity based on the EditCarRequest DTO
    @BeanMapping(nullValueCheckStrategy = ALWAYS, nullValuePropertyMappingStrategy = IGNORE)
    void update(EditCarRequest request, @MappingTarget Car car);
}
