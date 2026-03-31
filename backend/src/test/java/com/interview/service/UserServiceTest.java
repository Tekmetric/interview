package com.interview.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.AdditionalAnswers.returnsFirstArg;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.server.ResponseStatusException;

import com.interview.model.User;
import com.interview.repository.UserRepository;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private List<User> defaultUsers;
    private User defaultUser;

    private List<User> defaultAdmins;
    private User defaultAdmin;

    private static final String DEFAULT_ORGANIZATION = "MEKTETRIC";
    private static final String DEFAULT_NAME = "MARK";

    @Before
    public void setUp() {
        defaultUser = User.builder().name(DEFAULT_NAME).organization(DEFAULT_ORGANIZATION).build();
        defaultUsers = new ArrayList<>();
        defaultUsers.add(defaultUser);

        defaultAdmin = User.builder().name(DEFAULT_NAME).organization(DEFAULT_ORGANIZATION).administrator(true).build();
        defaultAdmins = new ArrayList<>();
        defaultAdmins.add(defaultAdmin);
    }

    @Test
    public void registerUserAsAdminOK() {
        
        when(userRepository.existsByNameAndOrganization(Mockito.anyString(), Mockito.anyString())).thenReturn(false);
        when(userRepository.existsByOrganization(Mockito.anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(returnsFirstArg());

        User registeredUser = userService.registerUser(defaultUser);
        assertEquals(registeredUser, defaultAdmin);
        assertTrue(registeredUser.isAdministrator());
    }

    @Test
    public void registerUserAsNonAdminOK() {
        
        when(userRepository.existsByNameAndOrganization(Mockito.anyString(), Mockito.anyString())).thenReturn(false);
        when(userRepository.existsByOrganization(Mockito.anyString())).thenReturn(true);
        when(userRepository.save(any(User.class))).thenAnswer(returnsFirstArg());

        User registeredUser = userService.registerUser(defaultUser);
        assertEquals(registeredUser, defaultUser);
        assertFalse(registeredUser.isAdministrator());
    }

    @Test(expected = ResponseStatusException.class)
    public void registerUserNotUnique() {

        when(userRepository.existsByNameAndOrganization(Mockito.anyString(), Mockito.anyString())).thenReturn(true);

        userService.registerUser(defaultUser);
    }

    @Test
    public void searchUserOK() {

        when(userRepository.findByNameAndOrganization(Mockito.anyString(), Mockito.anyString())).thenReturn(defaultUsers);

        User searchUser = userService.searchUsers(DEFAULT_NAME, DEFAULT_ORGANIZATION).get(0);
        assertEquals(searchUser, defaultUser);
    }

    @Test
    public void deleteUserOK() {

        when(userRepository.findByNameAndOrganization(Mockito.anyString(), Mockito.anyString())).thenReturn(defaultUsers);

        userService.deleteUser(DEFAULT_NAME, DEFAULT_ORGANIZATION);
        verify(userRepository, times(1)).delete(defaultUser);
    }

    @Test(expected = ResponseStatusException.class)
    public void deleteUserNotFound() {

        when(userRepository.findByNameAndOrganization(Mockito.anyString(), Mockito.anyString())).thenReturn(new ArrayList<>());

        userService.deleteUser(DEFAULT_NAME, DEFAULT_ORGANIZATION);
    }

    @Test(expected = ResponseStatusException.class)
    public void deleteUserAdminNonEmptyOrganization() {

        when(userRepository.findByNameAndOrganization(Mockito.anyString(), Mockito.anyString())).thenReturn(defaultAdmins);
        when(userRepository.countByOrganization(Mockito.anyString())).thenReturn(5);

        userService.deleteUser(DEFAULT_NAME, DEFAULT_ORGANIZATION);
    }
}
