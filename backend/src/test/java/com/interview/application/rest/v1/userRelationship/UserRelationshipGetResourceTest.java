package com.interview.application.rest.v1.userRelationship;

import com.interview.application.rest.v1.common.PathConstants;
import com.interview.application.rest.v1.user.dto.UserDto;
import com.interview.application.rest.v1.userRelationship.dto.ListOfFriendsDto;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserRelationshipGetResourceTest extends BaseUtilResourceIntegrationTest {
    @Autowired
    private UserRelationshipRepository userRelationshipRepository;

    @Override
    protected String getBaseAPIUrl() {
        return PathConstants.PATH_USER_RELATIONSHIPS;
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
     * This test tries to get a specific entity by its id.
     * <p>
     * HTTP 200 No Content should be received
     */
    @Test
    void testGetEntityHappyFlow() throws Exception {
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
        acceptRelationship(relationshipResponse);

        UserRelationship userRelationship = userRelationshipRepository.findOneById(relationshipResponse.getId()).orElseThrow();

        assertEquals(UserRelationshipState.ACCEPTED, userRelationship.getState());
        assertEquals(1L, userRelationship.getSender().getId());
        assertEquals(2L, userRelationship.getReceiver().getId());

        resultActions = getEntity(userRelationship.getId());
        resultActions.andExpect(status().isOk());
    }

    /**
     * This test tries to get the list of friends.
     * <p>
     * HTTP 200 No Content should be received
     */
    @Test
    void testGetFriendsOnlyOne() throws Exception {
        // no friends
        String getFriendsUrl = getBaseAPIUrl() + PathConstants.PATH_USER_RELATIONSHIPS_FRIENDS + PathConstants.SEPARATOR;
        ResultActions resultActions = restMvc.perform(get(getFriendsUrl + 1)
                .contentType(APPLICATION_JSON));
        resultActions.andExpect(status().isOk());
        ListOfFriendsDto listOfFriendsDto = readAnyObjectFromJsonBytes(
                resultActions.andReturn().getResponse().getContentAsByteArray(),
                ListOfFriendsDto.class);
        assertNotNull(listOfFriendsDto);
        assertNull(listOfFriendsDto.getFriends());

        // create the relationship between the users
        UserRelationshipDto userRelationshipRequest = UserRelationshipDto.builder()
                .sender(UserDto.builder().id(1L).build())
                .receiver(UserDto.builder().id(2L).build())
                .build();

        resultActions = createEntity(userRelationshipRequest);
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
        acceptRelationship(relationshipResponse);

        UserRelationship userRelationship = userRelationshipRepository.findOneById(relationshipResponse.getId()).orElseThrow();

        assertEquals(UserRelationshipState.ACCEPTED, userRelationship.getState());
        assertEquals(1L, userRelationship.getSender().getId());
        assertEquals(2L, userRelationship.getReceiver().getId());

        resultActions = getEntity(userRelationship.getId());
        resultActions.andExpect(status().isOk());

        // get the list of friends
        resultActions = restMvc.perform(get(getFriendsUrl + 1)
                .contentType(APPLICATION_JSON));
        resultActions.andExpect(status().isOk());

        listOfFriendsDto = readAnyObjectFromJsonBytes(
                resultActions.andReturn().getResponse().getContentAsByteArray(),
                ListOfFriendsDto.class);
        assertNotNull(listOfFriendsDto);
        assertEquals(1, listOfFriendsDto.getFriends().size());
        assertEquals(1, listOfFriendsDto.getNumberOfElements());
        assertEquals(0, listOfFriendsDto.getPageNumber());
        assertFalse(listOfFriendsDto.isHasNext());
        assertFalse(listOfFriendsDto.isHasPrevious());
        assertEquals("BP", listOfFriendsDto.getFriends().get(0).getInitials());
        assertEquals("bogdan-2 popa-2", listOfFriendsDto.getFriends().get(0).getDisplayName());
    }

    /**
     * This test tries to get the list of friends.
     * <p>
     * HTTP 200 No Content should be received
     */
    @Test
    void testGetFriendsMultiple() throws Exception {
        // no friends
        String getFriendsUrl = getBaseAPIUrl() + PathConstants.PATH_USER_RELATIONSHIPS_FRIENDS + PathConstants.SEPARATOR;
        ResultActions resultActions = restMvc.perform(get(getFriendsUrl + 1)
                .contentType(APPLICATION_JSON));
        resultActions.andExpect(status().isOk());
        ListOfFriendsDto listOfFriendsDto = readAnyObjectFromJsonBytes(
                resultActions.andReturn().getResponse().getContentAsByteArray(),
                ListOfFriendsDto.class);
        assertNotNull(listOfFriendsDto);
        assertNull(listOfFriendsDto.getFriends());

        // create the relationship between the users
        UserRelationshipDto userRelationshipRequest = UserRelationshipDto.builder()
                .sender(UserDto.builder().id(1L).build())
                .receiver(UserDto.builder().id(2L).build())
                .build();

        resultActions = createEntity(userRelationshipRequest);
        resultActions.andExpect(status().isCreated());
        UserRelationshipDto relationshipResponse_1_2 = readAnyObjectFromJsonBytes(
                resultActions.andReturn().getResponse().getContentAsByteArray(),
                UserRelationshipDto.class);

        assertNotNull(relationshipResponse_1_2);
        assertNotNull(relationshipResponse_1_2.getId());
        assertEquals(1L, relationshipResponse_1_2.getSender().getId());
        assertEquals(2L, relationshipResponse_1_2.getReceiver().getId());
        assertEquals(UserRelationshipState.PENDING, relationshipResponse_1_2.getState());

        acceptRelationship(relationshipResponse_1_2);

        // create the relationship between the users
        userRelationshipRequest = UserRelationshipDto.builder()
                .sender(UserDto.builder().id(1L).build())
                .receiver(UserDto.builder().id(5L).build())
                .build();

        resultActions = createEntity(userRelationshipRequest);
        resultActions.andExpect(status().isCreated());
        UserRelationshipDto relationshipResponse_1_5 = readAnyObjectFromJsonBytes(
                resultActions.andReturn().getResponse().getContentAsByteArray(),
                UserRelationshipDto.class);

        assertNotNull(relationshipResponse_1_5);
        assertNotNull(relationshipResponse_1_5.getId());
        assertEquals(1L, relationshipResponse_1_5.getSender().getId());
        assertEquals(5L, relationshipResponse_1_5.getReceiver().getId());
        assertEquals(UserRelationshipState.PENDING, relationshipResponse_1_5.getState());

        acceptRelationship(relationshipResponse_1_5);

        // create the relationship between the users
        userRelationshipRequest = UserRelationshipDto.builder()
                .sender(UserDto.builder().id(1L).build())
                .receiver(UserDto.builder().id(6L).build())
                .build();

        resultActions = createEntity(userRelationshipRequest);
        resultActions.andExpect(status().isCreated());
        UserRelationshipDto relationshipResponse_1_6 = readAnyObjectFromJsonBytes(
                resultActions.andReturn().getResponse().getContentAsByteArray(),
                UserRelationshipDto.class);

        assertNotNull(relationshipResponse_1_6);
        assertNotNull(relationshipResponse_1_6.getId());
        assertEquals(1L, relationshipResponse_1_6.getSender().getId());
        assertEquals(6L, relationshipResponse_1_6.getReceiver().getId());
        assertEquals(UserRelationshipState.PENDING, relationshipResponse_1_6.getState());

        acceptRelationship(relationshipResponse_1_6);

        // get the list of friends
        resultActions = restMvc.perform(get(getFriendsUrl + 1)
                .contentType(APPLICATION_JSON));
        resultActions.andExpect(status().isOk());

        listOfFriendsDto = readAnyObjectFromJsonBytes(
                resultActions.andReturn().getResponse().getContentAsByteArray(),
                ListOfFriendsDto.class);
        assertNotNull(listOfFriendsDto);
        assertEquals(3, listOfFriendsDto.getFriends().size());
        assertEquals(3, listOfFriendsDto.getNumberOfElements());
        assertEquals(0, listOfFriendsDto.getPageNumber());
        assertFalse(listOfFriendsDto.isHasNext());
        assertFalse(listOfFriendsDto.isHasPrevious());

        // get the list of friends with pagination
        resultActions = restMvc.perform(get(getFriendsUrl + 1 + "?page=1&size=2")
                .contentType(APPLICATION_JSON));
        resultActions.andExpect(status().isOk());

        listOfFriendsDto = readAnyObjectFromJsonBytes(
                resultActions.andReturn().getResponse().getContentAsByteArray(),
                ListOfFriendsDto.class);
        assertNotNull(listOfFriendsDto);
        assertEquals(1, listOfFriendsDto.getFriends().size());
        assertEquals(1, listOfFriendsDto.getNumberOfElements());
        assertEquals(1, listOfFriendsDto.getPageNumber());
        assertFalse(listOfFriendsDto.isHasNext());
        assertTrue(listOfFriendsDto.isHasPrevious());

        // get the list of friends with pagination
        resultActions = restMvc.perform(get(getFriendsUrl + 1 + "?page=0&size=2")
                .contentType(APPLICATION_JSON));
        resultActions.andExpect(status().isOk());

        listOfFriendsDto = readAnyObjectFromJsonBytes(
                resultActions.andReturn().getResponse().getContentAsByteArray(),
                ListOfFriendsDto.class);
        assertNotNull(listOfFriendsDto);
        assertEquals(2, listOfFriendsDto.getFriends().size());
        assertEquals(2, listOfFriendsDto.getNumberOfElements());
        assertEquals(0, listOfFriendsDto.getPageNumber());
        assertTrue(listOfFriendsDto.isHasNext());
        assertFalse(listOfFriendsDto.isHasPrevious());

        // get the list of friends with search
        resultActions = restMvc.perform(get(getFriendsUrl + 1 + "?search=popa-122")
                .contentType(APPLICATION_JSON));
        resultActions.andExpect(status().isOk());

        listOfFriendsDto = readAnyObjectFromJsonBytes(
                resultActions.andReturn().getResponse().getContentAsByteArray(),
                ListOfFriendsDto.class);
        assertNotNull(listOfFriendsDto);
        assertEquals(1, listOfFriendsDto.getFriends().size());
        assertEquals(1, listOfFriendsDto.getNumberOfElements());
        assertEquals(0, listOfFriendsDto.getPageNumber());
        assertFalse(listOfFriendsDto.isHasNext());
        assertFalse(listOfFriendsDto.isHasPrevious());

        // get the list of friends with search
        resultActions = restMvc.perform(get(getFriendsUrl + 1 + "?search=bogdan")
                .contentType(APPLICATION_JSON));
        resultActions.andExpect(status().isOk());

        listOfFriendsDto = readAnyObjectFromJsonBytes(
                resultActions.andReturn().getResponse().getContentAsByteArray(),
                ListOfFriendsDto.class);
        assertNotNull(listOfFriendsDto);
        assertEquals(3, listOfFriendsDto.getFriends().size());
        assertEquals(3, listOfFriendsDto.getNumberOfElements());
        assertEquals(0, listOfFriendsDto.getPageNumber());
        assertFalse(listOfFriendsDto.isHasNext());
        assertFalse(listOfFriendsDto.isHasPrevious());

        // get the list of friends with search
        resultActions = restMvc.perform(get(getFriendsUrl + 1 + "?search=bogdan-1&page=1&size=1")
                .contentType(APPLICATION_JSON));
        resultActions.andExpect(status().isOk());

        listOfFriendsDto = readAnyObjectFromJsonBytes(
                resultActions.andReturn().getResponse().getContentAsByteArray(),
                ListOfFriendsDto.class);
        assertNotNull(listOfFriendsDto);
        assertEquals(1, listOfFriendsDto.getFriends().size());
        assertEquals(1, listOfFriendsDto.getNumberOfElements());
        assertEquals(1, listOfFriendsDto.getPageNumber());
        assertFalse(listOfFriendsDto.isHasNext());
        assertTrue(listOfFriendsDto.isHasPrevious());
    }

    private void acceptRelationship(UserRelationshipDto relationshipResponse) throws Exception {
        String acceptedUrl = getBaseAPIUrl() + PathConstants.SEPARATOR
                + relationshipResponse.getId() + PathConstants.PATH_ACCEPTED;
        ResultActions resultActions = restMvc.perform(put(acceptedUrl)
                .contentType(APPLICATION_JSON));
        resultActions.andExpect(status().isOk());
    }
}
