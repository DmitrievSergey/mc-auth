package com.skillbox.auth;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillbox.auth.repository.UserRepository;
import com.skillbox.auth.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;



@AutoConfigureMockMvc
@ActiveProfiles({"test"})
@Testcontainers
public abstract class AbstractTest implements PostgreBaseTest {
    static  {
        POSTGRE_SQL_CONTAINER.start();
    }

    @Autowired
    protected UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @BeforeAll
    public static void beforeAll() {
        POSTGRE_SQL_CONTAINER.withEnv("spring.datasource.url", (p) -> POSTGRE_SQL_CONTAINER.getJdbcUrl());
        POSTGRE_SQL_CONTAINER.withEnv("spring.datasource.username", (p) -> POSTGRE_SQL_CONTAINER.getUsername());
        POSTGRE_SQL_CONTAINER.withEnv("spring.datasource.password", (p) -> POSTGRE_SQL_CONTAINER.getPassword());

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
