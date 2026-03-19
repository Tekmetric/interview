package com.interview.config;

import com.interview.repository.CarRepository;
import com.interview.repository.MechanicRepository;
import com.interview.repository.RepairOrderRepository;
import com.interview.service.RepairOrderService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfiguration {

  @Bean
  public RepairOrderService getRepairOrderService(
      RepairOrderRepository repairOrderRepository,
      CarRepository carRepository,
      MechanicRepository mechanicRepository) {
    return new RepairOrderService(repairOrderRepository, carRepository, mechanicRepository);
  }
}
