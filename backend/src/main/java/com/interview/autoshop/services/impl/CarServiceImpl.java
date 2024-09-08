package com.interview.autoshop.services.impl;

import com.interview.autoshop.dto.CarDto;
import com.interview.autoshop.dto.ClientDto;
import com.interview.autoshop.dto.create.CreateCarDto;
import com.interview.autoshop.exceptions.CarNotFoundException;
import com.interview.autoshop.exceptions.ClientNotFoundException;
import com.interview.autoshop.model.Car;
import com.interview.autoshop.model.Client;
import com.interview.autoshop.repositories.CarRepository;
import com.interview.autoshop.services.CarService;
import com.interview.autoshop.services.ClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;

    private final ClientService clientService;

    @Autowired
    public CarServiceImpl(final CarRepository carRepository, final ClientService clientService){
        this.carRepository = carRepository;
        this.clientService = clientService;
    }


    public CarDto entityToDto(Car car){
        return CarDto.builder()
                .id(car.getId())
                .vin(car.getVin())
                .owner(ClientDto.builder()
                        .id(car.getOwner().getId())
                        .phone(car.getOwner().getPhone())
                        .name(car.getOwner().getName())
                        .address(car.getOwner().getAddress())
                        .email(car.getOwner().getEmail())
                        .build())
                .make(car.getMake())
                .model(car.getModel())
                .color(car.getColor())
                .licensePlate(car.getLicensePlate())
                .build();
    }

    public Car createDtoToEntity(CreateCarDto carDto){
        Optional<Client> client = clientService.getEntityById(carDto.getOwnerId());
        if(!client.isPresent()){
            throw new ClientNotFoundException();
        }
        return Car.builder()
                .vin(carDto.getVin())
                .color(carDto.getColor())
                .make(carDto.getMake())
                .model(carDto.getModel())
                .licensePlate(carDto.getLicensePlate())
                .owner(client.get())
                .build();
    }

    @Override
    public Optional<CarDto> findById(Long id) {
        Optional<Car> car = carRepository.findById(id);
        return car.map(this::entityToDto);
    }

    @Override
    public Optional<Car> getEntityById(Long id) {
        return carRepository.findById(id);
    }

    @Override
    public List<CarDto> list(String email) {
        List<Car> cars = carRepository.findByOwnerEmailStartsWithIgnoreCase(email);
        return cars.stream().map(this::entityToDto).collect(Collectors.toList());
    }

    @Override
    public CarDto update(Long id, CreateCarDto carDto) {
        if(isCarPresent(id)){
            Car car = createDtoToEntity(carDto);
            car.setId(id);
            Car savedCar = carRepository.save(car);
            return entityToDto(savedCar);
        }
        else {
            throw new CarNotFoundException();
        }
    }

    @Override
    public void delete(Long id) {
        if(isCarPresent(id)){
            carRepository.deleteById(id);
        }
        else{
            log.debug("Car with id not found : " + id);
        }
    }

    @Override
    public boolean isCarPresent(Long id) {
        return carRepository.existsById(id);
    }

    @Override
    public CarDto create(CreateCarDto carDto) {
        Car car = createDtoToEntity(carDto);
        Car savedCar = carRepository.save(car);
        System.out.println("savedCar: " + savedCar);
        return entityToDto(savedCar);
    }
}
