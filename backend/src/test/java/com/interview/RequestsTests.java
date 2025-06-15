package com.interview;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.model.UserModel;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class RequestsTests {


    @Autowired
    private MockMvc mockMvc;

    private void addUsers(int amountOfUsers) throws Exception {
        for(int i = 0; i < amountOfUsers; i++){
            String userJson = "{\"email\":\"test"+i+"@gmail.com\",\"username\":\"test"+i+"\",\"password\":\"pass\"}";
            ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson));
            result.andExpect(MockMvcResultMatchers.status().isCreated());
        }
    }
    private Pair<List<Long>, List<UserModel>> addUsersAndGetIdsAndUsers (int amountOfUsers, ObjectMapper objectMapper) throws Exception{
        List<UserModel> users = new ArrayList<>();
        List<Long> ids = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            String userJson = "{\"email\":\"test"+i+"@gmail.com\",\"username\":\"test"+i+"\",\"password\":\"pass\"}";
            users.add(new UserModel("test"+i,"pass","test"+i+"@gmail.com"));
            ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson));
            result.andExpect(MockMvcResultMatchers.status().isCreated());
            String response = result.andReturn().getResponse().getContentAsString();
            // Convert the response JSON to a UserModel object
            UserModel responseUser = objectMapper.readValue(response, UserModel.class);
            ids.add(responseUser.getId());   
        }
        return Pair.of(ids, users);
    }

    @Test
    @Transactional
    public void testAddUser() throws Exception {
        addUsers(10);

        
        
    }
    @Test
    @Transactional
    public void testGetUsers() throws Exception {
        List<UserModel> users = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            String userJson = "{\"email\":\"test"+i+"@gmail.com\",\"username\":\"test"+i+"\",\"password\":\"pass\"}";
            users.add(new UserModel("test"+i,"pass","test"+i+"@gmail.com"));
            ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson));
            result.andExpect(MockMvcResultMatchers.status().isCreated());
        }
        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.get("/api/users")
            .contentType(MediaType.APPLICATION_JSON));
        result.andExpect(MockMvcResultMatchers.status().isOk());
        String response = result.andReturn().getResponse().getContentAsString();
        // Convert the response JSON to a list of UserModel objects
        ObjectMapper objectMapper = new ObjectMapper();
        List<UserModel> responseUsers = Arrays.asList(objectMapper.readValue(response, UserModel[].class));
        //need to compare responseUsers with users but need to ignore ID's
        for(int i = 0; i < users.size(); i++){
            assertEquals(users.get(i).getUsername(), responseUsers.get(i).getUsername());
            assertEquals(users.get(i).getEmail(), responseUsers.get(i).getEmail());
            assertEquals(users.get(i).getPassword(), responseUsers.get(i).getPassword());
        }
    }
    @Test
    @Transactional
    public void testGetUser() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Pair<List<Long>, List<UserModel>> idsAndUsers = addUsersAndGetIdsAndUsers(10, objectMapper);
        List<UserModel> users = idsAndUsers.getSecond();
        List<Long> ids = idsAndUsers.getFirst();
        for(int i = 0; i < users.size() ; i++){
            ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.get("/api/users/"+ids.get(i))
            .contentType(MediaType.APPLICATION_JSON));
            result.andExpect(MockMvcResultMatchers.status().isOk());
            String response = result.andReturn().getResponse().getContentAsString();
            // Convert the response JSON to a list of UserModel objects
            UserModel responseUser = objectMapper.readValue(response, UserModel.class);
            assertEquals(ids.get(i).longValue(), responseUser.getId());
            assertEquals(users.get(i).getUsername(), responseUser.getUsername());
            assertEquals(users.get(i).getEmail(), responseUser.getEmail());
            assertEquals(users.get(i).getPassword(), responseUser.getPassword());
        }
    }
    //TODO: Bugfix
    @Test
    @Transactional
    public void ensureNoDuplicates() throws Exception {
        List<Long> ids = addUsersAndGetIdsAndUsers(10, new ObjectMapper()).getFirst();
        ResultActions getUserResult = this.mockMvc.perform(MockMvcRequestBuilders.get("/api/users/"+ids.get(0))
            .contentType(MediaType.APPLICATION_JSON));
        getUserResult.andExpect(MockMvcResultMatchers.status().isOk());
        String response = getUserResult.andReturn().getResponse().getContentAsString();
        ResultActions conflictResult = this.mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(response));
        conflictResult.andExpect(MockMvcResultMatchers.status().isConflict());
        
        
    }
    @Test
    @Transactional
    public void updateUser() throws Exception {
        List<Long> ids = addUsersAndGetIdsAndUsers(10, new ObjectMapper()).getFirst();
        String updateJson = "{\"email\":\"testupdate@gmail.com\",\"username\":\"testupdate\",\"password\":\"passupdate\"}";
        ResultActions updateResult = this.mockMvc.perform(MockMvcRequestBuilders.put("/api/users/"+ids.get(0))
            .contentType(MediaType.APPLICATION_JSON)
            .content(updateJson));
        updateResult.andExpect(MockMvcResultMatchers.status().isOk());
        String response = updateResult.andReturn().getResponse().getContentAsString();
        // Convert the response JSON to a UserModel object
        ObjectMapper objectMapper = new ObjectMapper();
        UserModel responseUser = objectMapper.readValue(response, UserModel.class);
        assertEquals(ids.get(0).longValue(), responseUser.getId());
        assertEquals("testupdate", responseUser.getUsername());
        assertEquals("testupdate@gmail.com", responseUser.getEmail());
        assertEquals("passupdate", responseUser.getPassword());       
        
    }
    @Test
    @Transactional
    public void deleteUser() throws Exception {
        List<Long> ids = addUsersAndGetIdsAndUsers(10, new ObjectMapper()).getFirst();
        ResultActions deleteResult = this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/"+ids.get(0))
            .contentType(MediaType.APPLICATION_JSON));
        deleteResult.andExpect(MockMvcResultMatchers.status().isNoContent());
        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.get("/api/users/"+ids.get(0))
            .contentType(MediaType.APPLICATION_JSON));
            result.andExpect(MockMvcResultMatchers.status().isNotFound());
        
    }
    @Test
    @Transactional
    public void testGetUserNotFound() throws Exception {
        addUsers(10);
        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.get("/api/users/9999")
            .contentType(MediaType.APPLICATION_JSON));
        result.andExpect(MockMvcResultMatchers.status().isNotFound());
    }
    @Test
    @Transactional
    public void testDeleteAllUsers() throws Exception {
        addUsers(10);
        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/users")
            .contentType(MediaType.APPLICATION_JSON));
        result.andExpect(MockMvcResultMatchers.status().isNoContent());
    }
    @Test
    @Transactional
    public void testGetUserByEmail() throws Exception {
        Pair<List<Long>, List<UserModel>> idsAndUsers = addUsersAndGetIdsAndUsers(10, new ObjectMapper());
        List<Long> ids = idsAndUsers.getFirst();
        List<UserModel> users = idsAndUsers.getSecond();
        for(int i = 0; i < users.size() ; i++){
            ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.get("/api/users/email/"+users.get(i).getEmail())
            .contentType(MediaType.APPLICATION_JSON));
            result.andExpect(MockMvcResultMatchers.status().isOk());
            String response = result.andReturn().getResponse().getContentAsString();
            // Convert the response JSON to a list of UserModel objects
            UserModel responseUser = new ObjectMapper().readValue(response, UserModel.class);
            assertEquals(ids.get(i).longValue(), responseUser.getId());
            assertEquals(users.get(i).getUsername(), responseUser.getUsername());
            assertEquals(users.get(i).getEmail(), responseUser.getEmail());
            assertEquals(users.get(i).getPassword(), responseUser.getPassword());
        }
    }
    @Test
    @Transactional
    public void testGetUserByUsername() throws Exception{
        Pair<List<Long>, List<UserModel>> idsAndUsers = addUsersAndGetIdsAndUsers(10, new ObjectMapper());
        List<Long> ids = idsAndUsers.getFirst();
        List<UserModel> users = idsAndUsers.getSecond();
        for(int i = 0; i < users.size() ; i++){
            ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.get("/api/users/username/"+users.get(i).getUsername())
            .contentType(MediaType.APPLICATION_JSON));
            result.andExpect(MockMvcResultMatchers.status().isOk());
            String response = result.andReturn().getResponse().getContentAsString();
            // Convert the response JSON to a list of UserModel objects
            UserModel responseUser = new ObjectMapper().readValue(response, UserModel.class);
            assertEquals(ids.get(i).longValue(), responseUser.getId());
            assertEquals(users.get(i).getUsername(), responseUser.getUsername());
            assertEquals(users.get(i).getEmail(), responseUser.getEmail());
            assertEquals(users.get(i).getPassword(), responseUser.getPassword());
        }
    }
}
