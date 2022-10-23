package com.interview.application.rest.v1.user;

import com.interview.application.rest.v1.common.PathConstants;
import com.interview.application.rest.v1.user.dto.UserDto;
import com.interview.framework.BaseUtilResourceIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserGetResourceTest extends BaseUtilResourceIntegrationTest {

    @Override
    protected String getBaseAPIUrl() {
        return PathConstants.PATH_USERS;
    }

    /**
     * This test tries to make an GET request to read an entity without any id.
     * Should fall back to the get all operation that is not allowed.
     * <p>
     * HTTP 405 Not Allowed should be received
     */
    @Test
    void testReadEntityWithoutId() throws Exception {
        ResultActions resultActions = getEntity(null);
        resultActions.andExpect(status().isMethodNotAllowed());
    }

    /**
     * This test tries to make an GET request to read an entity that doesn't exist.
     * <p>
     * HTTP 404 Not Found should be received
     */
    @Test
    void testReadEntityWithInvalidId() throws Exception {
        ResultActions resultActions = getEntity(123);
        resultActions.andExpect(status().isNotFound());
    }

    /**
     * This test tries to make an GET request to read a valid entity.
     * <p>
     * HTTP 200 Ok should be received
     */
    @Test
    void testReadEntityHappyFlow() throws Exception {
        UserDto request = UserDto.builder()
                .email("dummyEmail@dummy.com")
                .password("dummyPassword")
                .build();
        ResultActions resultActions = createEntity(request);

        resultActions.andExpect(status().isCreated());
        UserDto createResponse = readAnyObjectFromJsonBytes(
                resultActions.andReturn().getResponse().getContentAsByteArray(),
                UserDto.class);

        resultActions = getEntity(createResponse.getId());
        resultActions.andExpect(status().isOk());

        UserDto getResponse = readAnyObjectFromJsonBytes(
                resultActions.andReturn().getResponse().getContentAsByteArray(),
                UserDto.class);

        assertNotNull(getResponse);
        assertEquals(createResponse.getId(), getResponse.getId());
        assertEquals(createResponse.getEmail(), getResponse.getEmail());
        assertEquals(createResponse.getPassword(), getResponse.getPassword());
        assertEquals(createResponse.getUserProfile(), getResponse.getUserProfile());
    }
}
