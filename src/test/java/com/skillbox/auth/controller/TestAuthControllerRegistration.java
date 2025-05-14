package com.skillbox.auth.controller;

import com.skillbox.auth.AbstractTest;
import com.skillbox.auth.dto.request.CreateUserDtoRequest;
import com.skillbox.auth.entity.CaptchaEntity;
import com.skillbox.auth.exception.CaptchaDoesNotMatchException;
import com.skillbox.auth.exception.PasswordsDoesNotMatchException;
import com.skillbox.auth.service.CaptchaService;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
@Sql(scripts = "classpath:db/cleanUp.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class TestAuthControllerRegistration extends AbstractTest {

    @MockitoBean
    CaptchaService captchaServiceMock;

    @MockitoBean
    ImageController imageController;

    @Test
    @DisplayName("Register user with correct captcha")
    public void testUserWithCorrectCaptchaCanRegister() throws Exception {
        Mockito.doReturn(getCaptchaEntity()).when(captchaServiceMock).getCaptchaById("123");


        post("/api/v1/auth/register", getUserDtoWithCorrectCaptcha())
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("Try register user with incorrect captcha")
    public void testUserWithInCorrectCaptchaCanTRegister() throws Exception {
        Mockito.doReturn(getCaptchaEntity()).when(captchaServiceMock).getCaptchaById("123");


        post("/api/v1/auth/register", getUserDtoWithInCorrectCaptcha())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof CaptchaDoesNotMatchException))
                .andExpect(result -> assertEquals("Каптча не совпадает", result.getResolvedException().getMessage()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error_description").value("Каптча не совпадает"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("400"))
                ;

    }

    @Test
    @DisplayName("User cant register with non matches passwords")
    public void testUserCantRegisterWithDifferentPasswords() throws Exception {
        Mockito.doReturn(getCaptchaEntity()).when(captchaServiceMock).getCaptchaById("123");


        post("/api/v1/auth/register", getUserDtoWithDifferentPasswords())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof PasswordsDoesNotMatchException))
                .andExpect(result -> assertEquals("Пароли не совпадают", result.getResolvedException().getMessage()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error_description").value("Пароли не совпадают"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("400"))
        ;

    }

    @Test
    @DisplayName("User cant register with existing email")
    @Sql(scripts = "classpath:db/createUser.sql")
    public void testUserWithExistingEmailCantRegister() throws Exception {
        Mockito.doReturn(getCaptchaEntity()).when(captchaServiceMock).getCaptchaById("123");

        post("/api/v1/auth/register", getUserDtoWithExistingEmail())
                .andExpect(status().isOk());
    }

    @NotNull
    private CreateUserDtoRequest getUserDtoWithCorrectCaptcha() {
        return new CreateUserDtoRequest(
                "Alex"
                , "Sergeev"
                , "password"
                , "password"
                , "alex2003@mail.ru"
        ,"testCaptcha"
        ,"123");
    }

    @NotNull
    private CreateUserDtoRequest getUserDtoWithDifferentPasswords() {
        return new CreateUserDtoRequest(
                "Alex"
                , "Sergeev"
                , "password1"
                , "password2"
                , "alex2003@mail.ru"
                ,"testCaptcha"
                ,"123");
    }

    @NotNull
    private CreateUserDtoRequest getUserDtoWithInCorrectCaptcha() {
        return new CreateUserDtoRequest(
                "Alex"
                , "Sergeev"
                , "password"
                , "password"
                , "alex2003@mail.ru"
                ,"testCaptcha1"
                ,"123");
    }

    @NotNull
    private CreateUserDtoRequest getUserDtoWithExistingEmail() {
        return new CreateUserDtoRequest(
                "Alex"
                , "Sergeev"
                , "password"
                , "password"
                , "alexIv@mail.ru"
                ,"testCaptcha"
                ,"123");
    }

    @NotNull
    private CaptchaEntity getCaptchaEntity() {
        return new CaptchaEntity(
                "123"
                , "testCaptcha"
        );
    }
}
