package com.mindhub.todolist.controllers;

import com.mindhub.todolist.config.JwtUtils;
import com.mindhub.todolist.dto.LoginUser;
import com.mindhub.todolist.dto.NewUserEntity;
import com.mindhub.todolist.dto.UserEntityDTO;
import com.mindhub.todolist.exceptions.EmailAlreadyExistsException;
import com.mindhub.todolist.models.UserEntity;
import com.mindhub.todolist.repositories.UserEntityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserEntityRepository userEntityRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtUtils jwtUtils;

    private UserEntityDTO newUserDTO;
    private LoginUser loginUser;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userEntityRepository.deleteAll();
        UserEntity userNew = new UserEntity("testUser", passwordEncoder.encode("password123"), "testuser@email.com");
        UserEntity createdUserEntity = userEntityRepository.save(userNew);
        newUserDTO = new UserEntityDTO(createdUserEntity);

        loginUser = new LoginUser("testuser@email.com", "password123");

        Mockito.when(jwtUtils.generateToken(Mockito.anyString()))
                .thenReturn("mocked-jwt-token");
    }

    @Test
    void testRegisterUser() throws Exception {
        NewUserEntity newUserEntity = new NewUserEntity("newUser", "password123", "newuser@example.com");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"newUser\", \"password\":\"password123\", \"email\":\"newuser@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("newUser")))
                .andExpect(jsonPath("$.email", is("newuser@example.com")));
    }

    @Test
    void testRegisterUser_EmailAlreadyExists() throws Exception {
        userEntityRepository.save(new UserEntity("existingUser", "password123", "existinguser@example.com"));

        NewUserEntity newUserEntity = new NewUserEntity("existingUser", "password123", "existinguser@example.com");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"existingUser\", \"password\":\"password123\", \"email\":\"existinguser@example.com\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Email already exists: existinguser@example.com"));
    }

    @Test
    @WithMockUser(username = "testuser@email.com", authorities = "USER")
    void testShouldAuthenticateUserSuccessfully() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginUser)))
                .andExpect(status().isOk()) // 200 OK
                .andExpect(content().string("mocked-jwt-token"));
    }


    @Test
    @WithMockUser(username = "testuser@email.com", authorities = "USER")
    void testShouldFailAuthentication() throws Exception {
        loginUser = new LoginUser("testuser@email.com", "password1");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginUser)))
                .andExpect(status().isForbidden());
    }
}