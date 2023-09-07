package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.RequestBuilder;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController userController;

    private UserRepository userRepository = mock(UserRepository.class);
    
    private CartRepository cartRepository = mock(CartRepository.class);

    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);
    
    @Before
    public void setUp(){
        
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);
    }
    
    @Test
    public void create_user_happy_path() throws Exception {
        when(encoder.encode("testPassword")).thenReturn("thisIsHashed");
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("test");
        request.setPassword("testPassword");
        request.setConfirmPassword("testPassword");

        final ResponseEntity<User> response = userController.createUser(request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User user = response.getBody();
        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals("test", user.getUsername());
        assertEquals("thisIsHashed", user.getPassword());
    }

    @Test
    public void error_create_user_password_length() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("test");
        request.setPassword("test");
        request.setConfirmPassword("test");

        final ResponseEntity<User> response = userController.createUser(request);

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    /*
     * How return Optional object: https://stackoverflow.com/questions/30946167/mockito-error-with-method-that-returns-optionalt
     */
    @Test
    public void find_user_by_id() throws Exception {
        User user = createUser("test", "thisIsHashed");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        final ResponseEntity<User> response = userController.findById(1L);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User retrievedUser = response.getBody();

        assertNotNull(retrievedUser);
        assertEquals(user.getId(), retrievedUser.getId());
        assertEquals(user.getUsername(), retrievedUser.getUsername());
        assertEquals(user.getPassword(), retrievedUser.getPassword());
    }

    @Test
    public void find_user_by_username() throws Exception {
        User user = createUser("test", "thisIsHashed");
        when(userRepository.findByUsername("test")).thenReturn(user);

        final ResponseEntity<User> response = userController.findByUserName("test");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User retrievedUser = response.getBody();

        assertNotNull(retrievedUser);
        assertEquals(user.getId(), retrievedUser.getId());
        assertEquals(user.getUsername(), retrievedUser.getUsername());
        assertEquals(user.getPassword(), retrievedUser.getPassword());
    }

    public User createUser(String username, String password){
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        return user;
    }

    /*@Test
    public void testCreateUserLoginScenario() throws Exception {
        userRequest.setUsername("jwttestname1");
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/user/create").content(objectMapper.writeValueAsString(userRequest))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.username").exists())
                .andReturn();
        MvcResult result = mockMvc
                .perform(MockMvcRequestBuilders.post("/login").content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk()).andReturn();
        request.addParameter("Authorization", result.getResponse().getHeader("Authorization"));
        assertNotNull(request.getParameter("Authorization"));
        userRequest.setUsername("DemoInput");
        mockMvc.perform(MockMvcRequestBuilders.post("/login").content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isUnauthorized()).andReturn();
        userRequest.setUsername("DemoInput");
        userRequest.setPassword("DemoPass");
        mockMvc.perform(MockMvcRequestBuilders.post("/login").content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isUnauthorized()).andReturn();
    }*/
}
