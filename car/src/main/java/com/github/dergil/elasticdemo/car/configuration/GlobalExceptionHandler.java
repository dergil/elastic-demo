package com.github.dergil.elasticdemo.car.configuration;

import com.github.dergil.elasticdemo.car.domain.exception.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

//    If requested ID is not found, the user gets a detailed explanation

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiCallError<String>> handleNotFoundException(HttpServletRequest request, EntityNotFoundException ex) {
        log.error("NotFoundException {}\n", request.getRequestURI(), ex);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiCallError<>("Not found exception", List.of(ex.getMessage())));
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApiCallError<T> {

        private String message;
        private List<T> details;

    }
}
