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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserRelationshipUpdateResourceTest extends BaseUtilResourceIntegrationTest {
    @Autowired
    private UserRelationshipRepository userRelationshipRepository;

    @Override
    protected String getBaseAPIUrl() {
        return PathConstants.PATH_USER_RELATIONSHIPS;
    }

    /**
     * This test tries to make an PUT request to update an entity without the id.
     * <p>
     * HTTP 405 Method Not Allowed should be received
     */
    @Test
    void testUpdateEntityWithoutId() throws Exception {
        UserRelationshipDto request = UserRelationshipDto.builder()
                .sender(UserDto.builder().build())
                .receiver(UserDto.builder().build())
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
     * This test tries to make an PUT request to update an entity.
     * <p>
     * HTTP 405 Method Not Allowed should be received
     */
    @Test
    void testUpdateEntity() throws Exception {
        UserRelationshipDto request = UserRelationshipDto.builder()
                .sender(UserDto.builder().build())
                .receiver(UserDto.builder().build())
                .build();
        ResultActions resultActions = updateEntity(123, request);
        resultActions.andExpect(status().isMethodNotAllowed());
    }

    /**
     * This test tries to make an PUT request to accept a user relationship request.
     * <p>
     * HTTP 200 OK should be received
     */
    @Test
    void testAcceptUserRelations() throws Exception {
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

        // the relationship is accepted again -- Bad Request
        resultActions = restMvc.perform(put(acceptedUrl)
                .contentType(APPLICATION_JSON));
        resultActions.andExpect(status().isBadRequest());

        String declined = getBaseAPIUrl() + PathConstants.SEPARATOR
                + relationshipResponse.getId() + PathConstants.PATH_DECLINED;
        // the relationship is declined again -- Bad Request
        resultActions = restMvc.perform(put(declined)
                .contentType(APPLICATION_JSON));
        resultActions.andExpect(status().isBadRequest());
    }

    /**
     * This test tries to make an PUT request to decline a user relationship request.
     * <p>
     * HTTP 200 OK should be received
     */
    @Test
    void testDeclineUserRelations() throws Exception {
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

        // the relationship is declined
        String declinedUrl = getBaseAPIUrl() + PathConstants.SEPARATOR
                + relationshipResponse.getId() + PathConstants.PATH_DECLINED;
        resultActions = restMvc.perform(put(declinedUrl)
                .contentType(APPLICATION_JSON));
        resultActions.andExpect(status().isOk());

        UserRelationship userRelationship = userRelationshipRepository.findOneById(relationshipResponse.getId()).orElseThrow();

        assertEquals(UserRelationshipState.DECLINED, userRelationship.getState());
        assertEquals(1L, userRelationship.getSender().getId());
        assertEquals(2L, userRelationship.getReceiver().getId());

        // the relationship is declined again -- Bad Request
        resultActions = restMvc.perform(put(declinedUrl)
                .contentType(APPLICATION_JSON));
        resultActions.andExpect(status().isBadRequest());

        // the relationship is accepted again -- Bad Request
        String acceptedUrl = getBaseAPIUrl() + PathConstants.SEPARATOR
                + relationshipResponse.getId() + PathConstants.PATH_ACCEPTED;
        resultActions = restMvc.perform(put(acceptedUrl)
                .contentType(APPLICATION_JSON));
        resultActions.andExpect(status().isBadRequest());
    }
}
