package com.skillbox.auth.controller;

import com.skillbox.auth.AbstractTest;
import com.skillbox.auth.dto.request.LoginDtoRequest;
import com.skillbox.auth.dto.response.AuthenticationDtoResponse;
import com.skillbox.auth.security.jwt.JwtTokenUtils;
import com.skillbox.auth.service.RefreshTokenService;
import com.skillbox.auth.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
@Sql(scripts = "classpath:db/cleanUp.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class TestAuthControllerLogin extends AbstractTest {
    @Autowired
    JwtTokenUtils jwtTokenUtils;

    @Autowired
    RefreshTokenService service;

    @Test
    @DisplayName("Log in user with correct credentials")
    @Sql(scripts = "classpath:db/createUser.sql")
    public void testUserWithCorrectCredentialsCanLogIn() throws Exception {

        ResultActions resultActions = post("/api/v1/auth/login", getUserDtoWithCorrectCredentials())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken").isString())
                .andExpect(MockMvcResultMatchers.jsonPath("$.refreshToken").isString());

        AuthenticationDtoResponse response = objectMapper
                .readValue(resultActions.andReturn().getResponse().getContentAsString()
                        , AuthenticationDtoResponse.class);
        String token = response.getAccessToken();
        String rToken = response.getRefreshToken();
        System.out.println("Access token " + token);
        assertEquals(jwtTokenUtils.getUserName(token),getUserDtoWithCorrectCredentials().getEmail());
        assertEquals(jwtTokenUtils.getUserId(token), "e913b22d-5d21-4998-ae8f-a258fca8913f");
        assertEquals(jwtTokenUtils.getFirstName(token), "Alexander");
        assertEquals(jwtTokenUtils.getLastName(token), "Ivanov");

    }

    @Test
    @DisplayName("User with incorrect credentials cant log in")
    @Sql(scripts = "classpath:db/createUser.sql")
    public void testUserWithInCorrectCredentialsCanTLogIn() throws Exception {

        ResultActions resultActions = post("/api/v1/auth/login", getUserDtoWithInCorrectCredentials())
                .andExpect(status().isUnauthorized());

    }


    private LoginDtoRequest getUserDtoWithCorrectCredentials() {
        return new LoginDtoRequest(
              "alexIv@mail.ru"
              ,"1234567890"
        );
    }

    private LoginDtoRequest getUserDtoWithInCorrectCredentials() {
        return new LoginDtoRequest(
                "alexIv@mail.ru"
                ,"1234567891"
        );
    }
}
