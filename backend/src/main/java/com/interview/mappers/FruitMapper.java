package com.interview.mappers;

import com.interview.model.Fruit;
import com.interview.model.FruitCreateRequest;
import com.interview.model.FruitResponse;
import com.interview.model.FruitPatchRequest;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;


@Mapper(componentModel = "spring", imports = {Instant.class, ChronoUnit.class})
public interface FruitMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "registrationDate", expression = "java(request.getRegistrationDate() != null " +
            "? request.getRegistrationDate()" +
            ": Instant.now().truncatedTo(ChronoUnit.MILLIS))")
    @Mapping(target = "lastUpdateDate", expression = "java(Instant.now().truncatedTo(ChronoUnit.MILLIS))")
    Fruit toEntity(FruitCreateRequest request);

    FruitResponse toResponse(Fruit fruit);

    List<FruitResponse> toResponse(List<Fruit> fruits);

    @Mapping(target = "registrationDate", expression = "java(request.getRegistrationDate() != null " +
            "? request.getRegistrationDate()" +
            ": Instant.now().truncatedTo(ChronoUnit.MILLIS))")
    @Mapping(target = "lastUpdateDate", expression = "java(Instant.now().truncatedTo(ChronoUnit.MILLIS))")
    void updateEntity(FruitCreateRequest request, @MappingTarget Fruit fruit);

    @Mapping(target = "lastUpdateDate", expression = "java(Instant.now().truncatedTo(ChronoUnit.MILLIS))")
    @BeanMapping(nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
    void patchEntity(FruitPatchRequest request, @MappingTarget Fruit fruit);
}
