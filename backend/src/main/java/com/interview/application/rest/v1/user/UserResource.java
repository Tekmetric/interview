package com.interview.application.rest.v1.user;

import com.interview.application.rest.v1.common.AbstractCRUDResource;
import com.interview.application.rest.v1.common.PathConstants;
import com.interview.application.rest.v1.common.annotations.CreateOperationIsSupported;
import com.interview.application.rest.v1.common.annotations.DeleteOperationIsSupported;
import com.interview.application.rest.v1.common.annotations.GetByIdOperationIsSupported;
import com.interview.application.rest.v1.common.annotations.UpdateOperationIsSupported;
import com.interview.application.rest.v1.user.dto.UserDto;
import com.interview.domain.model.User;
import com.interview.domain.service.common.AbstractCRUDService;
import com.interview.domain.service.model.UserService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * User spring boot resource.
 *
 * The following crud operations are enabled:
 * - create new entity
 * - get entity by id
 * - full entity update
 * - delete(logical) entity
 */
@RestController
@RequestMapping(value = PathConstants.PATH_USERS)
@CreateOperationIsSupported
@GetByIdOperationIsSupported
@UpdateOperationIsSupported
@DeleteOperationIsSupported
@RequiredArgsConstructor
public class UserResource extends AbstractCRUDResource<User, Long, UserDto> {

    private final UserService userService;

    @Override
    protected AbstractCRUDService<User, Long, UserDto> getService() {
        return userService;
    }
}
