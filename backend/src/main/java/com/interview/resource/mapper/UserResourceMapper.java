package com.interview.resource.mapper;

import com.interview.resource.model.UserDto;
import com.interview.resource.model.UserToSaveOrUpdateDto;
import com.interview.service.model.UserDm;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserResourceMapper {
    UserDto toDto(UserDm dm);

    UserDm toDm(Long id, UserToSaveOrUpdateDto dm);

    List<UserDto> toUserDtoList(List<UserDm> dm);
}
