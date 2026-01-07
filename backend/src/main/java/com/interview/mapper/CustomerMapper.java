package com.interview.mapper;

import com.interview.dto.CustomerDTO;
import com.interview.entity.CustomerEntity;

public class CustomerMapper {

    public static CustomerDTO toDTO(CustomerEntity entity) {
        if (entity == null) {
            return null;
        }
        CustomerDTO dto = new CustomerDTO();
        dto.setId(entity.getId());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setEmail(entity.getEmail());
        dto.setPhoneNumber(entity.getPhoneNumber());
        return dto;
    }

    public static CustomerEntity toEntity(CustomerDTO dto) {
        if (dto == null) {
            return null;
        }
        CustomerEntity entity = new CustomerEntity();
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setEmail(dto.getEmail());
        entity.setPhoneNumber(dto.getPhoneNumber());
        return entity;
    }

    public static void updateEntityFromDTO(CustomerDTO dto, CustomerEntity entity) {
        if (dto == null || entity == null) {
            return;
        }
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setEmail(dto.getEmail());
        entity.setPhoneNumber(dto.getPhoneNumber());
    }
}
