package com.interview.mapper;

import com.interview.dto.response.MechanicResponse;
import com.interview.model.Mechanic;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MechanicsMapper {

    List<MechanicResponse> toMechanicResponseList(List<Mechanic> mechanics);

}
