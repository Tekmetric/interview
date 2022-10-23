package com.interview.application.rest.v1.userRelationship;

import com.interview.application.rest.v1.common.AbstractCRUDResource;
import com.interview.application.rest.v1.common.PathConstants;
import com.interview.application.rest.v1.common.annotations.CreateOperationIsSupported;
import com.interview.application.rest.v1.common.annotations.DeleteOperationIsSupported;
import com.interview.application.rest.v1.common.annotations.GetByIdOperationIsSupported;
import com.interview.application.rest.v1.userRelationship.dto.ListOfFriendsDto;
import com.interview.application.rest.v1.userRelationship.dto.UserRelationshipDto;
import com.interview.domain.model.UserRelationship;
import com.interview.domain.model.enums.UserRelationshipState;
import com.interview.domain.service.common.AbstractCRUDService;
import com.interview.domain.service.model.UserRelationshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * User relation spring boot resource.
 *
 * The following crud operations are enabled:
 * - create new entity
 * - get entity by id
 * - delete(logical) entity
 */
@RestController
@RequestMapping(value = PathConstants.PATH_USER_RELATIONSHIPS)
@CreateOperationIsSupported
@GetByIdOperationIsSupported
@DeleteOperationIsSupported
@RequiredArgsConstructor
public class UserRelationshipResource extends AbstractCRUDResource<UserRelationship, Long, UserRelationshipDto> {
    private final UserRelationshipService userRelationshipService;

    @Override
    protected AbstractCRUDService<UserRelationship, Long, UserRelationshipDto> getService() {
        return userRelationshipService;
    }

    @PutMapping(value = "/{relationshipId}" + PathConstants.PATH_ACCEPTED)
    public ResponseEntity<?> updateEntityAccepted(final @PathVariable("relationshipId") Long id) {
        return ResponseEntity.ok(userRelationshipService.updateUserRelationshipState(
                id,
                UserRelationshipState.ACCEPTED));
    }

    @PutMapping(value = "/{relationshipId}" + PathConstants.PATH_DECLINED)
    public ResponseEntity<?> updateEntityDeclined(final @PathVariable("relationshipId") Long id) {
        return ResponseEntity.ok(userRelationshipService.updateUserRelationshipState(
                id,
                UserRelationshipState.DECLINED));
    }

    @GetMapping(PathConstants.PATH_USER_RELATIONSHIPS_FRIENDS + "/{userId}")
    public ResponseEntity<ListOfFriendsDto> getAllFriendsForUser(
            final @PathVariable("userId") Long userId,
            final @RequestParam(value = "page", required = false) Integer page,
            final @RequestParam(value = "size", required = false) Integer size,
            final @RequestParam(value = "search", required = false) String search) {
        return ResponseEntity.ok(userRelationshipService.findAllFriends(userId, page, size, search));
    }
}
