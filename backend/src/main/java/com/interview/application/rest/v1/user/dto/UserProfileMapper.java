package com.interview.application.rest.v1.user.dto;

import com.interview.domain.model.User;
import com.interview.domain.model.UserProfile;
import com.interview.domain.service.common.mapper.Mapper;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

/**
 * Mapper for the entity {@link UserProfile} and its Dto {@link UserProfileDto}.
 */
@org.mapstruct.Mapper(componentModel = "spring", config = Mapper.class)
public interface UserProfileMapper extends Mapper<UserProfile, UserProfileDto> {

    @Named("userProfileDTO")
    @Mapping(source = "user.id", target = "userId")
    UserProfileDto userProfileToUserProfileDTO(UserProfile userProfile);

    @Named("userProfile")
    @Mapping(source = "userId", target = "user")
    UserProfile userProfileDTOToUserProfile(UserProfileDto userProfileDTO);

    @InheritConfiguration(name = "updateEntityFromDTO")
    @Mapping(ignore = true, target = "user")
    UserProfile updateEntityFromDTO(UserProfileDto userProfileDTO, @MappingTarget UserProfile userProfile);

    @Override
    default UserProfile convertToEntity(UserProfileDto userProfileDTO) {
        return userProfileDTOToUserProfile(userProfileDTO);
    }

    @Override
    default UserProfileDto convertToDTO(UserProfile userProfile) {
        return userProfileToUserProfileDTO(userProfile);
    }

    default User userFromId(Long id) {
        if (id == null) {
            return null;
        }
        User user = new User();
        user.setId(id);
        return user;
    }
}
