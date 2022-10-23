package com.interview.application.rest.v1.userRelationship;

import com.interview.application.rest.v1.common.PathConstants;
import com.interview.application.rest.v1.user.dto.UserDto;
import com.interview.application.rest.v1.userRelationship.dto.UserRelationshipDto;
import com.interview.domain.model.UserRelationship;
import com.interview.domain.model.enums.UserRelationshipState;
import com.interview.domain.repository.UserRelationshipRepository;
import com.interview.framework.BaseUtilResourceIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserRelationshipDeleteResourceTest extends BaseUtilResourceIntegrationTest {
    @Autowired
    private UserRelationshipRepository userRelationshipRepository;

    @Override
    protected String getBaseAPIUrl() {
        return PathConstants.PATH_USER_RELATIONSHIPS;
    }

    /**
     * This test tries to make an DELETE request to delete an entity without any id.
     * <p>
     * HTTP 405 Not Allowed should be received
     */
    @Test
    void testDeleteEntityWithoutId() throws Exception {
        ResultActions resultActions = deleteEntity(null);
        resultActions.andExpect(status().isMethodNotAllowed());
    }

    /**
     * This test tries to make an DELETE request to delete an entity that doesn't exist.
     * <p>
     * HTTP 404 Not Found should be received
     */
    @Test
    void testDeleteEntityWithInvalidId() throws Exception {
        ResultActions resultActions = deleteEntity(123);
        resultActions.andExpect(status().isNotFound());
    }

    /**
     * This test tries to make an DELETE request to delete a valid entity.
     * <p>
     * HTTP 204 No Content should be received
     */
    @Test
    void testDeleteEntityHappyFlow() throws Exception {
        // create the relationship between the users
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

        // the relationship is accepted
        String acceptedUrl = getBaseAPIUrl() + PathConstants.SEPARATOR
                + relationshipResponse.getId() + PathConstants.PATH_ACCEPTED;
        resultActions = restMvc.perform(put(acceptedUrl)
                .contentType(APPLICATION_JSON));
        resultActions.andExpect(status().isOk());

        UserRelationship userRelationship = userRelationshipRepository.findOneById(relationshipResponse.getId()).orElseThrow();

        assertEquals(UserRelationshipState.ACCEPTED, userRelationship.getState());
        assertEquals(1L, userRelationship.getSender().getId());
        assertEquals(2L, userRelationship.getReceiver().getId());

        resultActions = deleteEntity(userRelationship.getId());
        resultActions.andExpect(status().isNoContent());

        assertFalse(userRelationshipRepository.findOneById(relationshipResponse.getId()).isPresent());

        resultActions = getEntity(userRelationship.getId());
        resultActions.andExpect(status().isNotFound());
    }
}
