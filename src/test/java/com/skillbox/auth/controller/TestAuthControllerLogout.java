package com.skillbox.auth.controller;

import com.skillbox.auth.AbstractTest;
import com.skillbox.auth.dto.request.LoginDtoRequest;
import com.skillbox.auth.dto.response.AuthenticationDtoResponse;
import com.skillbox.auth.entity.UserEntity;
import com.skillbox.auth.listeners.AuthListener;
import com.skillbox.auth.security.AppUserDetails;
import com.skillbox.auth.security.jwt.JwtTokenUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
public class TestAuthControllerLogout extends AbstractTest {

    @MockitoBean
    AuthListener authListener;

    @Autowired
    JwtTokenUtils jwtTokenUtils;

    @Test
    @DisplayName("Logout user")
    @Sql(scripts = "classpath:db/createUser.sql")
    @WithMockUser(username = "alexIv@mail.ru", roles = "USER")
    public void testUserCanLogout() throws Exception {

        ResultActions resultActions = post("/api/v1/auth/logout")
                .andExpect(status().isOk());
    }

    private LoginDtoRequest getUserDtoWithCorrectCredentials() {
        return new LoginDtoRequest(
                "alexIv@mail.ru"
                ,"1234567890"
        );
    }

    @NotNull
    private UserEntity getAuthorizedUser() {
        return new UserEntity(
                "e913b22d-5d21-4998-ae8f-a258fca8913f"
                , "Alexander"
                , "Ivanov"
                ,"$2a$12$MDXgqILUpMRFvR4PUGtZW.xyLPRxMbAnnvsCOvQZ0ApBrd11lPka6"
                , "alexIv@mail.ru"
                , new String[]{"ROLE_USER"}
        );
    }
}
