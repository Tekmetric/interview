package com.interview.application.rest.v1.user.dto;

import com.interview.domain.model.User;
import com.interview.domain.service.common.mapper.Mapper;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Mapper for the entity {@link User} and its Dto {@link UserDto}.
 */
@org.mapstruct.Mapper(componentModel = "spring", config = Mapper.class)
public interface UserMapper extends Mapper<User, UserDto> {

    @Mapping(source = "user.userProfile.id", target = "userProfile.userId")
    UserDto userToUserDTO(User user);

    @Mapping(ignore = true, target = "userProfile.user")
    User userDTOToUser(UserDto userDTO);

    @InheritConfiguration(name = "updateEntityFromDTO")
    @Mapping(ignore = true, target = "email")
    @Mapping(ignore = true, target = "password")
    @Mapping(ignore = true, target = "role")
    @Mapping(ignore = true, target = "userProfile.user")
    User updateEntityFromDTO(UserDto userDTO, @MappingTarget User user);

    @Override
    default User convertToEntity(UserDto userDTO) {
        return userDTOToUser(userDTO);
    }

    @Override
    default UserDto convertToDTO(User user) {
        return userToUserDTO(user);
    }
}
