package com.skillbox.auth;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillbox.auth.entity.UserEntity;
import com.skillbox.auth.repository.UserRepository;
import com.skillbox.auth.service.CaptchaService;
import com.skillbox.auth.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles({"test"})
@Testcontainers
public abstract class AbstractTest implements PostgreBaseTest {
    static  {
        POSTGRE_SQL_CONTAINER.start();
    }

    @Autowired
    protected CaptchaService hotelService;

    @Autowired
    protected UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeAll
    public static void beforeAll() {
        POSTGRE_SQL_CONTAINER.withEnv("spring.datasource.url", (p) -> POSTGRE_SQL_CONTAINER.getJdbcUrl());
        POSTGRE_SQL_CONTAINER.withEnv("spring.datasource.username", (p) -> POSTGRE_SQL_CONTAINER.getUsername());
        POSTGRE_SQL_CONTAINER.withEnv("spring.datasource.password", (p) -> POSTGRE_SQL_CONTAINER.getPassword());

    }


    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @BeforeEach
    private void setUp() {
//        UserEntity userAdmin = new UserEntity();
//        userAdmin.setEmail("admin@admin.ru");
//        userAdmin.setPassword("adminpassword");
//        userAdmin.setFirstName("Alex");
//        userAdmin.setRoles(new String[]{"ADMIN_ROLE"});
//        userService.saveUser(userAdmin);
//
//        UserEntity userUser = new UserEntity();
//        userUser.setEmail("roy@user.ru");
//        userUser.setPassword("userpassword");
//        userUser.setFirstName("Roy");
//        userUser.setRoles(new String[]{"USER_ROLE"});
//        userService.saveUser(userUser);
    }

    @AfterEach
    private void tearDown() {
        userRepository.deleteAll();
    }

    protected ResultActions post(String path, Object pathVariable, Object object) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders
                .post(path, pathVariable)
                .content(objectMapper.writeValueAsString(object))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));
    }

    protected ResultActions post(String path, Object object) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders
                .post(path).with(csrf())
                .content(objectMapper.writeValueAsString(object))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));
    }

    protected ResultActions post(String path, String pathParamName, String pathParam, Object object) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders
                .post(path).param(pathParamName, pathParam)
                .content(objectMapper.writeValueAsString(object))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));
    }

}
