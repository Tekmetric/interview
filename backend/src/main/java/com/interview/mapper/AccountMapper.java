package com.interview.mapper;

import com.interview.dto.account.response.AccountDetailsResponseDTO;
import com.interview.model.account.AccountEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * MapStruct mapper for converting Account entities to DTOs.
 */
@Mapper
public interface AccountMapper {

    AccountMapper INSTANCE = Mappers.getMapper(AccountMapper.class);

    /**
     * Convert AccountEntity to AccountDetailsResponseDTO.
     * Maps accountId field to accountReferenceId in DTO.
     * 
     * @param entity Account entity
     * @return AccountDetailsResponseDTO
     */
    @Mapping(source = "accountId", target = "accountReferenceId")
    AccountDetailsResponseDTO toDetailsDTO(AccountEntity entity);
}

