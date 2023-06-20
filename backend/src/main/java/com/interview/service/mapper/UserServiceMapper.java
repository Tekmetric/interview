package com.interview.service.mapper;

import com.interview.repository.model.UserEntity;
import com.interview.service.model.UserDm;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserServiceMapper {
    UserDm toDm(UserEntity entity);
}
