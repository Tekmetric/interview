package com.interview.service.mapper;

import com.interview.repository.model.UserEntity;
import com.interview.service.model.UserDm;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserServiceMapper {
    UserDm toDm(UserEntity entity);

    UserEntity toEntity(UserDm userDm);

    List<UserDm> toUserDmList(Iterable<UserEntity> entityList);
}
