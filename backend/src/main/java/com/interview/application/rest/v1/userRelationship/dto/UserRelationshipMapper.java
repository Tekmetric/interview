package com.interview.application.rest.v1.userRelationship.dto;

import com.interview.domain.model.UserRelationship;
import com.interview.domain.service.common.mapper.Mapper;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Mapper for the entity {@link UserRelationship} and its DTO {@link UserRelationshipDto}.
 */
@org.mapstruct.Mapper(componentModel = "spring", config = Mapper.class)
public interface UserRelationshipMapper extends Mapper<UserRelationship, UserRelationshipDto> {

    @Mapping(source = "sender.userProfile.id", target = "sender.userProfile.userId")
    @Mapping(source = "receiver.userProfile.id", target = "receiver.userProfile.userId")
    UserRelationshipDto userRelationshipToUserRelationshipDTO(UserRelationship userRelationship);

    @Mapping(ignore = true, target = "sender.userProfile.user")
    @Mapping(ignore = true, target = "receiver.userProfile.user")
    UserRelationship userRelationshipDTOToUserRelationship(UserRelationshipDto userRelationshipDTO);

    @InheritConfiguration(name = "updateEntityFromDTO")
    @Mapping(ignore = true, target = "sender")
    @Mapping(ignore = true, target = "receiver")
    UserRelationship updateEntityFromDTO(UserRelationshipDto userRelationshipDTO, @MappingTarget UserRelationship userRelationship);

    @Override
    default UserRelationship convertToEntity(UserRelationshipDto userRelationshipDTO) {
        return userRelationshipDTOToUserRelationship(userRelationshipDTO);
    }

    @Override
    default UserRelationshipDto convertToDTO(UserRelationship userRelationship) {
        return userRelationshipToUserRelationshipDTO(userRelationship);
    }
}
