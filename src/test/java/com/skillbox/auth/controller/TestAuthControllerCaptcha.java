package com.skillbox.auth.controller;

import com.skillbox.auth.AbstractTest;
import com.skillbox.auth.listeners.AuthListener;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
public class TestAuthControllerCaptcha extends AbstractTest {
    @MockitoBean
    AuthListener authListener;
    @Test
    @DisplayName("Get captcha")
    public void testUserWithCorrectCredentialsCanLogIn() throws Exception {

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/auth/captcha")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }
}
