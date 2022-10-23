package com.interview.domain.service.model;

import com.interview.application.rest.v1.user.dto.UserDto;
import com.interview.domain.exception.ApplicationException;
import com.interview.domain.exception.ErrorCode;
import com.interview.domain.exception.ErrorDetail;
import com.interview.domain.model.User;
import com.interview.domain.model.UserProfile;
import com.interview.domain.model.enums.Role;
import com.interview.domain.repository.EntityRepository;
import com.interview.domain.repository.UserRepository;
import com.interview.domain.service.common.AbstractCRUDService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
@Slf4j
public class UserService extends AbstractCRUDService<User, Long, UserDto> {
    private final UserRepository userRepository;
    private final UserProfileService userProfileService;

    public UserService(
            final UserRepository userRepository,
            final UserProfileService userProfileService) {
        super(User.class);
        this.userRepository = userRepository;
        this.userProfileService = userProfileService;
    }

    @Override
    protected EntityRepository<User, Long> getRepository() {
        return userRepository;
    }

    @Override
    protected User newEntity(final UserDto userDto) {
        return convertToEntity(userDto);
    }

    @Override
    protected void onBeforeSaveNewEntity(final User entity) {
        Optional<User> existingUser = userRepository.findOneByEmail(entity.getEmail());
        if (existingUser.isPresent()){
            log.error("Email already exists for request=[{}]", entity);
            final ErrorDetail errorDetail = new ErrorDetail(ErrorCode.USER_EMAIL_ALREADY_EXISTS);
            throw new ApplicationException(errorDetail);
        }
        if (Objects.isNull(entity.getRole())) {
            entity.setRole(Role.ROLE_USER);
        }

        UserProfile userProfile = entity.getUserProfile();
        if (userProfile == null) {
            entity.setUserProfile(new UserProfile());
        }
    }

    @Override
    protected void onAfterSaveNewEntity(final User entity) {
        UserProfile userProfile = entity.getUserProfile();
        userProfile.setUser(entity);
    }

    @Override
    public UserDto convertToDto(final User entity) {
        UserDto userDTO = super.convertToDto(entity);
        userDTO.setUserProfile(userProfileService.convertToDto(entity.getUserProfile()));

        return userDTO;
    }
}
