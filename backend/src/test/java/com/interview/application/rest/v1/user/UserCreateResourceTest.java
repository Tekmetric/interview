package com.interview.application.rest.v1.user;

import com.interview.application.rest.v1.common.PathConstants;
import com.interview.application.rest.v1.user.dto.UserDto;
import com.interview.application.rest.v1.user.dto.UserProfileDto;
import com.interview.domain.model.User;
import com.interview.domain.model.enums.Role;
import com.interview.domain.repository.UserRepository;
import com.interview.framework.BaseUtilResourceIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.time.Month;

import static com.interview.domain.exception.ErrorI18nKey.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.containsInAnyOrder;

class UserCreateResourceTest extends BaseUtilResourceIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Override
    protected String getBaseAPIUrl() {
        return PathConstants.PATH_USERS;
    }

    /**
     * This test tries to make an POST request to create a new entity that is null.
     * <p>
     * HTTP 400 Bad Request should be received
     */
    @Test
    void testCreateEntityNullBody() throws Exception {
        ResultActions resultActions = createEntity(null);
        resultActions.andExpect(status().isBadRequest());
    }

    /**
     * This test tries to make an POST request to create a new entity with invalid body.
     * <p>
     * HTTP 400 Bad Request should be received
     */
    @Test
    void testCreateEntityWithInvalidRequest() throws Exception {
        // Use case 1 - body is empty
        UserDto request = new UserDto();
        ResultActions resultActions = createEntity(request);

        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("description")
                        .value(internationalizationService.getTranslation(BAD_REQUEST_ERROR_I18N_KEY)))
                .andExpect(jsonPath("fieldErrors").isArray())
                .andExpect(jsonPath("fieldErrors.length()").value(2))
                .andExpect(jsonPath("fieldErrors.[*].field")
                        .value(containsInAnyOrder("password", "email")))
                .andExpect(jsonPath("fieldErrors.[*].message")
                        .value(containsInAnyOrder("must not be null", "must not be null")));

        // Use case 2 - body doesn't have any email
        request = UserDto.builder().password("dummyPassword").build();
        resultActions = createEntity(request);

        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("description")
                        .value(internationalizationService.getTranslation(BAD_REQUEST_ERROR_I18N_KEY)))
                .andExpect(jsonPath("fieldErrors").isArray())
                .andExpect(jsonPath("fieldErrors.length()").value(1))
                .andExpect(jsonPath("fieldErrors.[*].field")
                        .value(containsInAnyOrder("email")))
                .andExpect(jsonPath("fieldErrors.[*].message")
                        .value(containsInAnyOrder( "must not be null")));

        // Use case 3 - body doesn't have any password
        request = UserDto.builder().email("dummyEmail@dummy.com").build();
        resultActions = createEntity(request);

        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("description")
                        .value(internationalizationService.getTranslation(BAD_REQUEST_ERROR_I18N_KEY)))
                .andExpect(jsonPath("fieldErrors").isArray())
                .andExpect(jsonPath("fieldErrors.length()").value(1))
                .andExpect(jsonPath("fieldErrors.[*].field")
                        .value(containsInAnyOrder("password")))
                .andExpect(jsonPath("fieldErrors.[*].message")
                        .value(containsInAnyOrder( "must not be null")));

        // Use case 4 - invalid email format
        request = UserDto.builder()
                .email("dummyEmail")
                .password("dummyPassword")
                .build();
        resultActions = createEntity(request);

        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("description")
                        .value(internationalizationService.getTranslation(BAD_REQUEST_ERROR_I18N_KEY)))
                .andExpect(jsonPath("fieldErrors").isArray())
                .andExpect(jsonPath("fieldErrors.length()").value(1))
                .andExpect(jsonPath("fieldErrors.[*].field")
                        .value(containsInAnyOrder("email")))
                .andExpect(jsonPath("fieldErrors.[*].message")
                        .value(containsInAnyOrder( "must be a well-formed email address")));

        // Use case 5 - email too long
        request = UserDto.builder()
                .email("dummyEmaildummyEmaildummyEmaildummyEmaildummyEmaildummyEmail@dummy.com")
                .password("dummyPassword")
                .build();
        resultActions = createEntity(request);

        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("description")
                        .value(internationalizationService.getTranslation(BAD_REQUEST_ERROR_I18N_KEY)))
                .andExpect(jsonPath("fieldErrors").isArray())
                .andExpect(jsonPath("fieldErrors.length()").value(1))
                .andExpect(jsonPath("fieldErrors.[*].field")
                        .value(containsInAnyOrder("email")))
                .andExpect(jsonPath("fieldErrors.[*].message")
                        .value(containsInAnyOrder( "size must be between 1 and 50")));

        // Use case 6 - password too short
        request = UserDto.builder()
                .email("dummyEmail@dummy.com")
                .password("dummy")
                .build();
        resultActions = createEntity(request);

        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("description")
                        .value(internationalizationService.getTranslation(BAD_REQUEST_ERROR_I18N_KEY)))
                .andExpect(jsonPath("fieldErrors").isArray())
                .andExpect(jsonPath("fieldErrors.length()").value(1))
                .andExpect(jsonPath("fieldErrors.[*].field")
                        .value(containsInAnyOrder("password")))
                .andExpect(jsonPath("fieldErrors.[*].message")
                        .value(containsInAnyOrder( "size must be between 8 and 50")));

        // Use case 7 - password too long
        request = UserDto.builder()
                .email("dummyEmail@dummy.com")
                .password("dummyPassworddummyPassworddummyPassworddummyPassworddummyPassword")
                .build();
        resultActions = createEntity(request);

        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("description")
                        .value(internationalizationService.getTranslation(BAD_REQUEST_ERROR_I18N_KEY)))
                .andExpect(jsonPath("fieldErrors").isArray())
                .andExpect(jsonPath("fieldErrors.length()").value(1))
                .andExpect(jsonPath("fieldErrors.[*].field")
                        .value(containsInAnyOrder("password")))
                .andExpect(jsonPath("fieldErrors.[*].message")
                        .value(containsInAnyOrder( "size must be between 8 and 50")));
    }

    /**
     * This test tries to make an POST request to create a
     * new entity with min required data.
     * <p>
     * HTTP 201 Created should be received
     */
    @Test
    void testCreateEntityWithMinDataRequest() throws Exception {
        UserDto request = UserDto.builder()
                .email("dummyEmail@dummy.com")
                .password("dummyPassword")
                .build();
        ResultActions resultActions = createEntity(request);

        resultActions.andExpect(status().isCreated());
        UserDto response = readAnyObjectFromJsonBytes(
                resultActions.andReturn().getResponse().getContentAsByteArray(),
                UserDto.class);

        assertNotNull(response);
        assertNotNull(response.getId());
        assertNotNull(response.getUserProfile());
        assertNotNull(response.getUserProfile().getId());
        assertNull(response.getUserProfile().getFirstName());
        assertNull(response.getUserProfile().getLastName());
        assertEquals(request.getEmail(), response.getEmail());
        assertEquals(request.getPassword(), response.getPassword());
        assertEquals(Role.ROLE_USER, response.getRole());

        User userFromDb = userRepository.findOneById(response.getId()).orElseThrow();
        assertNotNull(userFromDb);
        assertNotNull(userFromDb.getCreatedBy());
        assertNotNull(userFromDb.getUserProfile().getCreatedBy());
        assertNotNull(userFromDb.getCreatedDate());
        assertNotNull(userFromDb.getUserProfile().getCreatedDate());
        assertNotNull(userFromDb.getLastModifiedBy());
        assertNotNull(userFromDb.getUserProfile().getLastModifiedBy());
        assertNotNull(userFromDb.getLastModifiedDate());
        assertNotNull(userFromDb.getUserProfile().getLastModifiedDate());
    }

    /**
     * This test tries to make an POST request to create a
     * new entity with min required data.
     * <p>
     * HTTP 201 Created should be received
     */
    @Test
    void testCreateEntityWithFullDataRequest() throws Exception {
        UserDto request = UserDto.builder()
                .email("dummyEmail@dummy.com")
                .password("dummyPassword")
                .userProfile(UserProfileDto.builder()
                        .firstName("firstName")
                        .lastName("lastName")
                        .phoneNumber("123456789")
                        .dateOfBirth(LocalDate.of(1985, Month.JANUARY, 25))
                        .build())
                .build();
        ResultActions resultActions = createEntity(request);

        resultActions.andExpect(status().isCreated());
        UserDto response = readAnyObjectFromJsonBytes(
                resultActions.andReturn().getResponse().getContentAsByteArray(),
                UserDto.class);

        assertNotNull(response);
        assertNotNull(response.getId());
        assertNotNull(response.getUserProfile());
        assertNotNull(response.getUserProfile().getId());
        assertEquals(request.getUserProfile().getFirstName(), response.getUserProfile().getFirstName());
        assertEquals(request.getUserProfile().getLastName(), response.getUserProfile().getLastName());
        assertEquals(request.getUserProfile().getPhoneNumber(), response.getUserProfile().getPhoneNumber());
        assertEquals(request.getUserProfile().getDateOfBirth(), response.getUserProfile().getDateOfBirth());
        assertEquals(request.getEmail(), response.getEmail());
        assertEquals(request.getPassword(), response.getPassword());
        assertEquals(Role.ROLE_USER, response.getRole());
    }

    /**
     * This test tries to make create multiple users with the same email
     * <p>
     * HTTP 400 Bad Request should be received.
     */
    @Test
    void testMultipleUsersWithSameEmail() throws Exception {
        UserDto request = UserDto.builder()
                .email("dummyEmail@dummy.com")
                .password("dummyPassword")
                .build();
        ResultActions resultActions = createEntity(request);
        resultActions.andExpect(status().isCreated());

        resultActions = createEntity(request);
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("description")
                        .value(internationalizationService.getTranslation(USER_EMAIL_ALREADY_EXISTS_ERROR_I18N_KEY)));
    }
}
