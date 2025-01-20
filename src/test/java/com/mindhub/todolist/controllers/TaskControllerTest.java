package com.mindhub.todolist.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindhub.todolist.config.JwtUtils;
import com.mindhub.todolist.dto.*;
import com.mindhub.todolist.models.RoleType;
import com.mindhub.todolist.models.Task;
import com.mindhub.todolist.models.TaskStatus;
import com.mindhub.todolist.models.UserEntity;
import com.mindhub.todolist.repositories.TaskRepository;
import com.mindhub.todolist.repositories.UserEntityRepository;
import com.mindhub.todolist.services.TaskService;
import com.mindhub.todolist.services.UserEntityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Mock
    private TaskService taskService;

    @Mock
    private UserEntityService userEntityService;

    @InjectMocks
    private TaskController taskController;

    private ObjectMapper objectMapper;

    @Autowired
    private UserEntityRepository userEntityRepository;

    @Autowired
    private TaskRepository taskRepository;

    private final String validToken = "Bearer mocked-valid-jwt-token";

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtUtils jwtUtils;

    private LoginUser loginUser;
    private UserEntityDTO newUserDTO;
    private TaskDTO newTaskDTO;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        userEntityRepository.deleteAll();
        UserEntity userNew = new UserEntity("testUser", passwordEncoder.encode("password123"), "testuser@email.com");
        userNew.setRole(RoleType.ADMIN);
        UserEntity createdUserEntity = userEntityRepository.save(userNew);
        newUserDTO = new UserEntityDTO(createdUserEntity);

        loginUser = new LoginUser("testuser@email.com", "password123");

        User userDetails =
                new User(
                        "testuser@email.com",
                        "password123",
                        List.of(new SimpleGrantedAuthority("USER"))
                );

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
        SecurityContextHolder.setContext(new SecurityContextImpl(authentication));

        Mockito.when(jwtUtils.generateToken(Mockito.anyString()))
                .thenReturn("mocked-jwt-token");
    }

    @Test
    @DisplayName("Should create a new task")
    void testShouldCreateNewTask() throws Exception {
        UserEntityId userId = new UserEntityId(1L);
        String title = "Test Task";
        String description = "This is a test task";
        TaskStatus status = TaskStatus.PENDING;

        NewTask newTask = new NewTask(title, description, status, userId);

        mockMvc.perform(post("/api/tasks/current-user")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTask)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(title))
                .andExpect(jsonPath("$.description").value(description))
                .andExpect(jsonPath("$.status").value(status.toString()));
    }

    @Test
    @DisplayName("Should get task by id")
    @WithMockUser(username = "testuser@email.com", authorities = "ADMIN")
    void testShouldGetATaskById() throws Exception {
        Task task = new Task("Test Task", "This is a test task", TaskStatus.PENDING);
        UserEntity owner = new UserEntity("getTestUser", "password12345", "gettestuser@email.com");
        userEntityRepository.save(owner);
        task.setUserEntity(owner);
        owner.addTask(task);
        taskRepository.save(task);

        Long taskId = task.getId();

        mockMvc.perform(get("/api/tasks/{id}", taskId)
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskId))
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andExpect(jsonPath("$.description").value("This is a test task"))
                .andExpect(jsonPath("$.status").value(TaskStatus.PENDING.toString()));
    }
}
