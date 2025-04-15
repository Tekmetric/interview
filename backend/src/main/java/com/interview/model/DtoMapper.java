package com.interview.model;

import com.interview.model.db.Car;
import com.interview.model.db.Job;
import com.interview.model.db.Task;
import com.interview.model.dto.JobCreateRequest;
import com.interview.model.dto.JobResponse;
import com.interview.model.dto.TaskRequest;
import com.interview.model.dto.TaskResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface DtoMapper {

    DtoMapper Instance = Mappers.getMapper(DtoMapper.class);

    @Mapping(source = "carId", target = "id")
    Car toCarEntity(JobCreateRequest jobCreateRequest);

    @Mapping(source = "car.vin", target = "vin")
    @Mapping(source = "car.make", target = "make")
    @Mapping(source = "car.model", target = "model")
    @Mapping(source = "car.modelYear", target = "modelYear")
    @Mapping(source = "car.customer", target = "customer")
    JobResponse toJobResponse(Job job);

    List<JobResponse> toJobResponses(Iterable<Job> job);

    Task toTaskEntity(TaskRequest taskRequest);

    TaskResponse toTaskResponse(Task task);
}
