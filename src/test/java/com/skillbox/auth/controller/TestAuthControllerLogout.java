package com.skillbox.auth.controller;

import com.skillbox.auth.AbstractTest;
import com.skillbox.auth.dto.response.AuthenticationDtoResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
public class TestAuthControllerLogout extends AbstractTest {
    @Test
    @DisplayName("Logout user")
    @Sql(scripts = "classpath:db/createUser.sql")
    public void testUserCanLogout() throws Exception {

        ResultActions resultActions = post("/api/v1/auth/logout")
                .andExpect(status().isOk());
    }
}
