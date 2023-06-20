package com.interview.resource;

import com.interview.resource.model.UserDto;
import com.interview.resource.model.UserToSaveDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class UserResourceIntegrationTest {
    @Autowired
    UserResource userResource;

    @Test
    void getAll_expectedDefaultUsersExist() {
        List<UserDto> actualUsers = userResource.getUsers().getBody();

        assert actualUsers != null;
        assertEquals(2, actualUsers.size());
        assertEquals("Minerva", actualUsers.get(0).getFirstname());
        assertEquals("McGonagall", actualUsers.get(0).getLastname());

        assertEquals("Severus", actualUsers.get(1).getFirstname());
        assertEquals("Snape", actualUsers.get(1).getLastname());
    }

    @Test
    void getById_expectedDefaultFirstUserExists() {
        UserDto actualUser = userResource.findById(1L).getBody();

        assertEquals("Minerva", actualUser.getFirstname());
        assertEquals("McGonagall", actualUser.getLastname());
    }

    @Test
    void createUser_expectedSuccessfullyCreatedUser() {
        UserToSaveDto expectedUser = UserToSaveDto.builder()
                .firstname("Harry")
                .lastname("Potter")
                .build();

        userResource.saveNewUser(expectedUser);

        List<UserDto> actualUsers = userResource.getUsers().getBody();

        assert actualUsers != null;
        assertEquals(3, actualUsers.size());
        assertEquals(expectedUser.getFirstname(), actualUsers.get(2).getFirstname());
        assertEquals(expectedUser.getLastname(), actualUsers.get(2).getLastname());
    }
}
