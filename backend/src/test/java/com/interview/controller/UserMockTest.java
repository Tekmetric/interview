package com.interview.controller;

import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import com.interview.entity.User;
import com.interview.service.UserServiceImpl;

@WebMvcTest(UserController.class)
@Import(UserMockTest.TestConfig.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserMockTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserServiceImpl userServiceImpl;

	@TestConfiguration
	static class TestConfig {
		@Bean
		public ModelMapper modelMapper() {
			return new ModelMapper();
		}
	}



	@Test
	void testListUsersWithoutPagination() throws Exception {
		// Create mock users
		User user1 = new User();
		user1.setUserId(1L);
		user1.setUserName("John Doe");
		user1.setUserEmail("john@example.com");
		user1.setUserPhone("1234567890");

		User user2 = new User();
		user2.setUserId(2L);
		user2.setUserName("Jane Doe");
		user2.setUserEmail("jane@example.com");
		user2.setUserPhone("0987654321");

		List<User> mockUsers = Arrays.asList(user1, user2);

		// Mock the service method
		when(userServiceImpl.listUsers()).thenReturn(mockUsers);

		// Perform GET request and verify (without pagination parameters)
		mockMvc.perform(get("/api/users"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].userId").value(1))
			.andExpect(jsonPath("$[0].userName").value("John Doe"))
			.andExpect(jsonPath("$[0].userEmail").value("john@example.com"))
			.andExpect(jsonPath("$[0].userPhone").value("1234567890"))
			.andExpect(jsonPath("$[1].userId").value(2))
			.andExpect(jsonPath("$[1].userName").value("Jane Doe"))
			.andExpect(jsonPath("$[1].userEmail").value("jane@example.com"))
			.andExpect(jsonPath("$[1].userPhone").value("0987654321"));
	}

	@Test
	void testAddUser() throws Exception {
		// Create mock user
		User user = new User();
		user.setUserId(1L);
		user.setUserName("John Doe");
		user.setUserEmail("john@example.com");
		user.setUserPhone("1234567890");

		// Mock the service method
		when(userServiceImpl.addUser(org.mockito.ArgumentMatchers.any(User.class))).thenReturn(user);

		// JSON request body
		String userJson = """
			{
				"userName": "John Doe",
				"userEmail": "john@example.com",
				"userPassword": "password123",
				"userPhone": "1234567890"
			}
			""";

		// Perform POST request and verify
		mockMvc.perform(post("/api/users")
				.contentType(APPLICATION_JSON)
				.content(userJson))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.userId").value(1))
			.andExpect(jsonPath("$.userName").value("John Doe"))
			.andExpect(jsonPath("$.userEmail").value("john@example.com"))
			.andExpect(jsonPath("$.userPhone").value("1234567890"));
	}

	@Test
	void testModifyUser() throws Exception {
		// Create mock user
		User user = new User();
		user.setUserId(1L);
		user.setUserName("John Doe Updated");
		user.setUserEmail("john.updated@example.com");
		user.setUserPhone("1234567890");

		// Mock the service method
		when(userServiceImpl.modifyUser(org.mockito.ArgumentMatchers.any(User.class))).thenReturn(user);

		// JSON request body
		String userJson = """
			{
				"userId": 1,
				"userName": "John Doe Updated",
				"userEmail": "john.updated@example.com",
				"userPassword": "password123",
				"userPhone": "1234567890"
			}
			""";

		// Perform PUT request and verify
		mockMvc.perform(put("/api/users")
				.contentType(APPLICATION_JSON)
				.content(userJson))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.userId").value(1))
			.andExpect(jsonPath("$.userName").value("John Doe Updated"))
			.andExpect(jsonPath("$.userEmail").value("john.updated@example.com"))
			.andExpect(jsonPath("$.userPhone").value("1234567890"));
	}
	@Test
	void testModifyUserWithInvalidEmail() throws Exception {
		// JSON request body with invalid email
		String userJson = """
			{
				"userId": 1,
				"userName": "John Doe",
				"userEmail": "invalid-email",
				"userPassword": "password123",
				"userPhone": "1234567890"
			}
			""";

		// Perform PUT request and expect bad request due to validation
		mockMvc.perform(put("/api/users")
				.contentType(APPLICATION_JSON)
				.content(userJson))
			.andExpect(status().isBadRequest());
	}

}
