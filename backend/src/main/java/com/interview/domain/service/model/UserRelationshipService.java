package com.interview.domain.service.model;

import com.interview.application.rest.v1.userRelationship.dto.FriendDto;
import com.interview.application.rest.v1.userRelationship.dto.ListOfFriendsDto;
import com.interview.application.rest.v1.userRelationship.dto.UserRelationshipDto;
import com.interview.domain.exception.ApplicationException;
import com.interview.domain.exception.ErrorCode;
import com.interview.domain.exception.ErrorDetail;
import com.interview.domain.model.User;
import com.interview.domain.model.UserProfile;
import com.interview.domain.model.UserRelationship;
import com.interview.domain.model.enums.UserRelationshipState;
import com.interview.domain.repository.EntityRepository;
import com.interview.domain.repository.UserRelationshipRepository;
import com.interview.domain.service.common.AbstractCRUDService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.interview.domain.model.enums.UserRelationshipState.*;

@Service
@Transactional
public class UserRelationshipService extends AbstractCRUDService<UserRelationship, Long, UserRelationshipDto> {
    private static final Logger LOG = LoggerFactory.getLogger(UserRelationshipService.class);

    private final UserRelationshipRepository userRelationshipRepository;
    private final UserService userService;

    public UserRelationshipService(
            final UserRelationshipRepository userRelationshipRepository,
            final UserService userService) {
        super(UserRelationship.class);
        this.userRelationshipRepository = userRelationshipRepository;
        this.userService = userService;
    }

    @Override
    protected EntityRepository<UserRelationship, Long> getRepository() {
        return userRelationshipRepository;
    }

    @Override
    protected UserRelationship newEntity(UserRelationshipDto userRelationshipDTO) {
        return convertToEntity(userRelationshipDTO);
    }

    @Override
    protected void onBeforeSaveNewEntity(UserRelationship entity) {
        final User senderUser = userService.get(entity.getSender().getId());
        if (ObjectUtils.isEmpty(senderUser)) {
            LOG.error("Error occurred while creating user relationship, sendrUser is invalid");
            throw new ApplicationException(new ErrorDetail(ErrorCode.BAD_REQUEST));
        }

        final User requestedUser = userService.get(entity.getReceiver().getId());
        if (ObjectUtils.isEmpty(requestedUser)) {
            LOG.error("Error occurred while creating user relationship, requestedUser is invalid");
            throw new ApplicationException(new ErrorDetail(ErrorCode.BAD_REQUEST));
        }

        entity.setSender(senderUser);
        entity.setReceiver(requestedUser);
        if (entity.getSender().getId().equals(entity.getReceiver().getId())) {
            LOG.error("Error occurred while creating friend request, sender and receiver should be different");
            throw new ApplicationException(new ErrorDetail(ErrorCode.BAD_REQUEST));
        }

        Optional<UserRelationship> existingUserRelationshipOptional =
                userRelationshipRepository.findUserRelationshipByTwoUserIds(
                        entity.getSender().getId(),
                        entity.getReceiver().getId());
        if (existingUserRelationshipOptional.isPresent()) {
            UserRelationship existingUserRelationship = existingUserRelationshipOptional.get();
            UserRelationshipState existingUserRelationshipState = existingUserRelationship.getState();
            if (existingUserRelationshipState.equals(ACCEPTED) || existingUserRelationshipState.equals(PENDING)) {
                LOG.error("Error occurred while creating friend request, because a friend request already exists between user = [{}] and user = [{}]",
                        entity.getSender().getId(),
                        entity.getReceiver().getId());
                throw new ApplicationException(new ErrorDetail(ErrorCode.BAD_REQUEST));
            } else {
                LOG.info("The old relationship is deleted, because we create a new one...");
                // delete the old user relationship and create a new one
                getRepository().delete(existingUserRelationship);
            }
        }
        entity.setState(PENDING);
    }

    @Override
    protected void onBeforeDeleteEntity(UserRelationship entity) {
        validateRequest(entity, DELETED);
        entity.setState(DELETED);
    }

    private void validateRequest(
            final UserRelationship userRelationship,
            final UserRelationshipState state) {
        boolean isValid = true;
        switch (state) {
            case ACCEPTED:
            case DECLINED:
                if (!userRelationship.getState().equals(PENDING)) {
                    isValid = false;
                }
                break;
            case DELETED:
                if (!userRelationship.getState().equals(ACCEPTED) && !userRelationship.getState().equals(PENDING)) {
                    isValid = false;
                }
        }

        if (!isValid) {
            LOG.error("Error occurred, userRelationship = [{}] cannot be updated to the new status [{}]", userRelationship.getId(), state);
            throw new ApplicationException(new ErrorDetail(ErrorCode.BAD_REQUEST));
        }
    }

    public UserRelationshipDto updateUserRelationshipState(Long userRelationshipId, UserRelationshipState newState) {
        final UserRelationship userRelationship = get(userRelationshipId);

        if (ObjectUtils.isEmpty(userRelationship)) {
            LOG.error("Error occurred while updating user relationship, no active friend request could be found with id = [{}]", userRelationshipId);
            throw new ApplicationException(new ErrorDetail(ErrorCode.INTERNAL_ERROR));
        }

        validateRequest(userRelationship, newState);
        userRelationship.setState(newState);
        return convertToDto(save(userRelationship));
    }

    public ListOfFriendsDto findAllFriends(
            final Long userId,
            final Integer page,
            final Integer size,
            final String search) {
        Slice<UserRelationship> friends = userRelationshipRepository.getFriends(userId, ACCEPTED, search, createPageRequest(page, size, null));

        if (friends.hasContent()) {
            return buildGetAllFriendsResponse(userId, friends.getContent(), friends);
        }
        return new ListOfFriendsDto();
    }

    private ListOfFriendsDto buildGetAllFriendsResponse(Long userId, List<UserRelationship> userRelationships, Slice<?> slice) {
        if (userRelationships == null || userRelationships.isEmpty()) {
            return new ListOfFriendsDto();
        }

        List<FriendDto> friends = userRelationships.stream()
                .map(userRelationship -> buildFriendDto(userRelationship, userId))
                .collect(Collectors.toList());

        return new ListOfFriendsDto(friends, slice.getNumber(), slice.getNumberOfElements(), slice.hasNext(), slice.hasPrevious());
    }

    private FriendDto buildFriendDto(UserRelationship userRelationship, Long userId) {
        User user = userRelationship.getReceiver().getId().equals(userId) ? userRelationship.getSender() : userRelationship.getReceiver();
        UserProfile userProfile = user.getUserProfile();
        return new FriendDto(userId, userRelationship.getId(), userProfile.computeDisplayName(), userProfile.computeInitials());
    }
}
