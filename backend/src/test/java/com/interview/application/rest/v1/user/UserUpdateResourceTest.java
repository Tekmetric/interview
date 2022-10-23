package com.interview.application.rest.v1.user;

import com.interview.application.rest.v1.common.PathConstants;
import com.interview.application.rest.v1.user.dto.UserDto;
import com.interview.domain.model.enums.Role;
import com.interview.framework.BaseUtilResourceIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;

import static com.interview.domain.exception.ErrorI18nKey.BAD_REQUEST_ERROR_I18N_KEY;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserUpdateResourceTest extends BaseUtilResourceIntegrationTest {

    @Override
    protected String getBaseAPIUrl() {
        return PathConstants.PATH_USERS;
    }

    /**
     * This test tries to make an PUT request to update an entity without the id.
     * <p>
     * HTTP 405 Method Not Allowed should be received
     */
    @Test
    void testUpdateEntityWithoutId() throws Exception {
        UserDto request = UserDto.builder()
                .email("dummyEmail@dummy.com")
                .password("password")
                .build();
        ResultActions resultActions = updateEntity(null, request);
        resultActions.andExpect(status().isMethodNotAllowed());
    }

    /**
     * This test tries to make an PUT request to update an entity without the body.
     * <p>
     * HTTP 400 Bad Request should be received
     */
    @Test
    void testUpdateEntityWithoutBody() throws Exception {
        ResultActions resultActions = updateEntity(123, null);
        resultActions.andExpect(status().isBadRequest());
    }

    /**
     * This test tries to make an PUT request to update a new entity with invalid body.
     * <p>
     * HTTP 400 Bad Request should be received
     */
    @Test
    void testUpdateEntityWithInvalidRequest() throws Exception {
        // Use case 1 - body is empty
        UserDto request = new UserDto();
        ResultActions resultActions = updateEntity(123, request);

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
        resultActions = updateEntity(123, request);

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
        resultActions = updateEntity(123, request);

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
        resultActions = updateEntity(123, request);

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
        resultActions = updateEntity(123, request);

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
        resultActions = updateEntity(123, request);

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
        resultActions = updateEntity(123, request);

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

    @Test
    void testUpdateEntityWithInvalidId() throws Exception {
        UserDto request = UserDto.builder()
                .email("dummyEmail@dummy.com")
                .password("dummyPassword")
                .build();
        ResultActions resultActions = createEntity(request);

        resultActions.andExpect(status().isCreated());

        resultActions = updateEntity(123, request);
        resultActions.andExpect(status().isNotFound());
    }

    /**
     * This test tries to make an PUT request to update an
     * entity with min required data.
     * <p>
     * HTTP 200 Created should be received
     */
    @Test
    void testUpdateEntity() throws Exception {
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
        assertNull(response.getUserProfile().getDateOfBirth());
        assertEquals(request.getEmail(), response.getEmail());
        assertEquals(request.getPassword(), response.getPassword());
        assertEquals(Role.ROLE_USER, response.getRole());

        response.setEmail("newDummyEmail@dummy.com");
        response.setPassword("newDummyPassword");
        response.getUserProfile().setFirstName("dummyFirstName");
        response.getUserProfile().setDateOfBirth(LocalDate.of(2020, 10 , 10));

        resultActions = updateEntity(response.getId(), response);
        resultActions.andExpect(status().isOk());

        UserDto updateResponse = readAnyObjectFromJsonBytes(
                resultActions.andReturn().getResponse().getContentAsByteArray(),
                UserDto.class);
        assertNotNull(updateResponse);
        assertNotNull(updateResponse.getId());
        assertNotNull(updateResponse.getUserProfile());
        assertNotNull(updateResponse.getUserProfile().getId());
        assertEquals("dummyFirstName", updateResponse.getUserProfile().getFirstName());
        assertEquals(
                LocalDate.of(2020, 10 , 10),
                updateResponse.getUserProfile().getDateOfBirth());
        assertNull(updateResponse.getUserProfile().getLastName());
        assertEquals(request.getEmail(), updateResponse.getEmail());
        assertEquals(request.getPassword(), updateResponse.getPassword());
        assertEquals(Role.ROLE_USER, response.getRole());
    }
}
