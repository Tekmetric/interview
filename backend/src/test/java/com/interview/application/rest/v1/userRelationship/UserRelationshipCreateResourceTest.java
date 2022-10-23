package com.interview.application.rest.v1.userRelationship;

import com.interview.application.rest.v1.common.PathConstants;
import com.interview.application.rest.v1.user.dto.UserDto;
import com.interview.application.rest.v1.userRelationship.dto.UserRelationshipDto;
import com.interview.domain.model.enums.UserRelationshipState;
import com.interview.framework.BaseUtilResourceIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import static com.interview.domain.exception.ErrorI18nKey.BAD_REQUEST_ERROR_I18N_KEY;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserRelationshipCreateResourceTest extends BaseUtilResourceIntegrationTest {


    @Override
    protected String getBaseAPIUrl() {
        return PathConstants.PATH_USER_RELATIONSHIPS;
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
        UserRelationshipDto request = new UserRelationshipDto();
        ResultActions resultActions = createEntity(request);

        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("description")
                        .value(internationalizationService.getTranslation(BAD_REQUEST_ERROR_I18N_KEY)))
                .andExpect(jsonPath("fieldErrors").isArray())
                .andExpect(jsonPath("fieldErrors.length()").value(2))
                .andExpect(jsonPath("fieldErrors.[*].field")
                        .value(containsInAnyOrder("receiver", "sender")))
                .andExpect(jsonPath("fieldErrors.[*].message")
                        .value(containsInAnyOrder("must not be null", "must not be null")));

        // Use case 2 - body doesn't have any sender user
        request = UserRelationshipDto.builder().receiver(new UserDto()).build();
        resultActions = createEntity(request);

        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("description")
                        .value(internationalizationService.getTranslation(BAD_REQUEST_ERROR_I18N_KEY)))
                .andExpect(jsonPath("fieldErrors").isArray())
                .andExpect(jsonPath("fieldErrors.length()").value(1))
                .andExpect(jsonPath("fieldErrors.[*].field")
                        .value(containsInAnyOrder("sender")))
                .andExpect(jsonPath("fieldErrors.[*].message")
                        .value(containsInAnyOrder( "must not be null")));

        // Use case 3 - body doesn't have any receiver user
        request = UserRelationshipDto.builder().sender(new UserDto()).build();
        resultActions = createEntity(request);

        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("description")
                        .value(internationalizationService.getTranslation(BAD_REQUEST_ERROR_I18N_KEY)))
                .andExpect(jsonPath("fieldErrors").isArray())
                .andExpect(jsonPath("fieldErrors.length()").value(1))
                .andExpect(jsonPath("fieldErrors.[*].field")
                        .value(containsInAnyOrder("receiver")))
                .andExpect(jsonPath("fieldErrors.[*].message")
                        .value(containsInAnyOrder( "must not be null")));
    }

    /**
     * This test tries to make an POST request to create a
     * new entity with min required data.
     * <p>
     * HTTP 201 Created should be received
     */
    @Test
    void testCreateEntityHappyCase() throws Exception {
        UserRelationshipDto userRelationshipRequest = UserRelationshipDto.builder()
                .sender(UserDto.builder().id(1L).build())
                .receiver(UserDto.builder().id(2L).build())
                .build();

        ResultActions resultActions = createEntity(userRelationshipRequest);
        resultActions.andExpect(status().isCreated());
        UserRelationshipDto relationshipResponse = readAnyObjectFromJsonBytes(
                resultActions.andReturn().getResponse().getContentAsByteArray(),
                UserRelationshipDto.class);

        assertNotNull(relationshipResponse);
        assertNotNull(relationshipResponse.getId());
        assertEquals(1L, relationshipResponse.getSender().getId());
        assertEquals(2L, relationshipResponse.getReceiver().getId());
        assertEquals(UserRelationshipState.PENDING, relationshipResponse.getState());

        resultActions = createEntity(userRelationshipRequest);
        resultActions.andExpect(status().isBadRequest());
    }
}
