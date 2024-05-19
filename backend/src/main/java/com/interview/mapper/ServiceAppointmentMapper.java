package com.interview.mapper;


import com.interview.dto.ServiceAppointmentDTO;
import com.interview.entity.ServiceAppointment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", uses = {CustomerMapper.class})
public interface ServiceAppointmentMapper {
    @Mapping(source = "customer.id", target = "customerId")
    ServiceAppointmentDTO toDTO(ServiceAppointment serviceAppointment);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "customerId", target = "customer.id")
    ServiceAppointment toEntity(ServiceAppointmentDTO serviceAppointmentDTO);
}
