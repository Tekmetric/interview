package com.interview.controller;

import com.interview.service.CarMakeService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CarMakeController {


    private final CarMakeService carMakeService;

    public CarMakeController(CarMakeService carMakeService) {
        this.carMakeService = carMakeService;
    }
}
