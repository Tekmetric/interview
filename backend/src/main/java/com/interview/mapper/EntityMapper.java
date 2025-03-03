package com.interview.mapper;

import com.interview.dto.AnimalDTO;
import com.interview.dto.CreateAnimalDTO;
import com.interview.dto.CreateEmployeeDTO;
import com.interview.dto.CreateVetDTO;
import com.interview.dto.EmployeeDTO;
import com.interview.dto.VetDTO;
import com.interview.model.Animal;
import com.interview.model.Employee;
import com.interview.model.Vet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface EntityMapper {

    @Mapping(target = "responsibleEmployeeId", source = "responsibleEmployee.id")
    @Mapping(target = "vetIds", source = "vets", qualifiedByName = "vetsToIds")
    AnimalDTO toDTO(Animal animal);

    @Mapping(target = "animalIds", source = "animals", qualifiedByName = "animalsToIds")
    EmployeeDTO toDTO(Employee employee);

    @Mapping(target = "animalIds", source = "animals", qualifiedByName = "animalsToIds")
    VetDTO toDTO(Vet vet);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "responsibleEmployee", ignore = true)
    @Mapping(target = "vets", ignore = true)
    Animal toEntity(CreateAnimalDTO dto);

    @Mapping(target = "responsibleEmployee", ignore = true)
    @Mapping(target = "vets", ignore = true)
    Animal toEntity(AnimalDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "animals", ignore = true)
    Employee toEntity(CreateEmployeeDTO dto);

    @Mapping(target = "animals", ignore = true)
    Employee toEntity(EmployeeDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "animals", ignore = true)
    Vet toEntity(CreateVetDTO dto);

    @Mapping(target = "animals", ignore = true)
    Vet toEntity(VetDTO dto);

    @Named("vetsToIds")
    default Set<Long> vetsToIds(Set<Vet> vets) {
        if (vets == null || vets.isEmpty()) {
            return null;
        }
        return vets.stream()
                .map(Vet::getId)
                .collect(Collectors.toSet());
    }

    @Named("animalsToIds")
    default Set<Long> animalsToIds(Set<Animal> animals) {
        if (animals == null) {
            return null;
        }
        return animals.stream()
                .map(Animal::getId)
                .collect(Collectors.toSet());
    }
}