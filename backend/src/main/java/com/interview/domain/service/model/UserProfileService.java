package com.interview.domain.service.model;

import com.interview.application.aspect.log.annotations.ExcludeFromLoggingAspect;
import com.interview.application.rest.v1.user.dto.UserProfileDto;
import com.interview.domain.model.UserProfile;
import com.interview.domain.repository.EntityRepository;
import com.interview.domain.repository.UserProfileRepository;
import com.interview.domain.service.common.AbstractCRUDService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserProfileService extends AbstractCRUDService<UserProfile, Long, UserProfileDto> {

  private final UserProfileRepository userProfileRepository;

  public UserProfileService(final UserProfileRepository userProfileRepository) {
    super(UserProfile.class);
    this.userProfileRepository = userProfileRepository;
  }

  @Override
  protected EntityRepository<UserProfile, Long> getRepository() {
    return userProfileRepository;
  }

  @Override
  protected UserProfile newEntity(final UserProfileDto entityDto) {
    return convertToEntity(entityDto);
  }

  @Override
  @ExcludeFromLoggingAspect
  public UserProfileDto convertToDto(final UserProfile entity) {
    return super.convertToDto(entity);
  }
}
