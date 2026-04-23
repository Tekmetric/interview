package com.interview.autoshop;

import com.interview.autoshop.dao.AutoshopDao;
import com.interview.autoshop.dto.AutoshopResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AutoshopMapper {

    Autoshop toDomain(AutoshopDao dao);

    AutoshopResponse toResponse(Autoshop domain);
}
