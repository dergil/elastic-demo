package com.github.dergil.elasticdemo.car.repository;

import com.github.dergil.elasticdemo.car.domain.model.Car;
import com.github.dergil.elasticdemo.car.domain.exception.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
//    throws EntityNotFoundException if the object could not be found
    default Car getCarById(long id) throws EntityNotFoundException {
        return findById(id).orElseThrow(() -> new EntityNotFoundException(Car.class, id));
    }
}
