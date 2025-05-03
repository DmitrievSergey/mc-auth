package com.skillbox.auth.kafka;

import com.skillbox.auth.AbstractTest;
import com.skillbox.auth.KafkaBaseTest;
import com.skillbox.auth.controller.ImageController;
import com.skillbox.auth.dto.events.ChangeEmailEvent;
import com.skillbox.auth.dto.events.UserRegistrationEvent;
import com.skillbox.auth.dto.request.CreateUserDtoRequest;
import com.skillbox.auth.dto.request.EmailRequest;
import com.skillbox.auth.entity.CaptchaEntity;
import com.skillbox.auth.entity.UserEntity;
import com.skillbox.auth.security.AppUserDetails;
import com.skillbox.auth.security.jwt.JwtTokenUtils;
import com.skillbox.auth.service.CaptchaService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;


import java.time.Duration;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static java.util.Objects.requireNonNull;
import static org.apache.kafka.clients.consumer.ConsumerConfig.*;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.kafka.listener.ContainerProperties.AckMode.MANUAL;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
@Sql(scripts = "classpath:db/cleanUp.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class KafkaTests extends AbstractTest implements KafkaBaseTest {

    @Autowired
    JwtTokenUtils jwtTokenUtils;

    @MockitoBean
    CaptchaService captchaServiceMock;

    @MockitoBean
    ImageController imageController;


    private static KafkaMessageListenerContainer<String, ?> clientConsumer;

    private static final BlockingQueue<ConsumerRecord<String, ?>> CLIENT_CONSUMER_RECORDS =
            new LinkedBlockingQueue<>(1);

    @BeforeAll
    static void setUp() {
        final var clientConsumerProps = new HashMap<String, Object>();
        clientConsumerProps.put(BOOTSTRAP_SERVERS_CONFIG, KAFKA_CONTAINER.getBootstrapServers());
        clientConsumerProps.put(GROUP_ID_CONFIG, "socialNetwork-group-id");
        clientConsumerProps.put(AUTO_OFFSET_RESET_CONFIG, "earliest");
        clientConsumerProps.put(ALLOW_AUTO_CREATE_TOPICS_CONFIG, "true");
        clientConsumerProps.put(MAX_POLL_RECORDS_CONFIG, "1");
        clientConsumerProps.put(ENABLE_AUTO_COMMIT_CONFIG, "false");
        clientConsumerProps.put(MAX_POLL_INTERVAL_MS_CONFIG, "60000");


        final var deserializer = new JsonDeserializer<Object>();
        deserializer.addTrustedPackages("*");
        final var clientConsumerFactory = new DefaultKafkaConsumerFactory<>(
                clientConsumerProps,
                new StringDeserializer(),
                deserializer
        );

        final var clientConsumerContainerProperties = new ContainerProperties("REGISTER_TOP", "ACCOUNT_CHANGES");
        clientConsumerContainerProperties.setAckMode(MANUAL);
        clientConsumer = new KafkaMessageListenerContainer<>(clientConsumerFactory, clientConsumerContainerProperties);

        clientConsumer.setupMessageListener(
                (AcknowledgingMessageListener<String, ?>) (data, acknowledgment) -> {
                    try {
                        CLIENT_CONSUMER_RECORDS.put(data);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    requireNonNull(acknowledgment).acknowledge();
                }

        );
        clientConsumer.start();
    }

    @AfterAll
    static void stop() {
        clientConsumer.stop();
    }

    @Test
    @DisplayName("Test send register event")
    public void testSendRegistrationEventAfterRegistrationComplete() throws Exception {

        Mockito.doReturn(getCaptchaEntity()).when(captchaServiceMock).getCaptchaById("123");


        post("/api/v1/auth/register", getUserDtoWithCorrectCaptcha())
                .andExpect(status().isOk());

        await()
                .pollInterval(Duration.ofSeconds(3))
                .atMost(Duration.ofSeconds(15))
                .untilAsserted(() -> {
                    final var response = Optional.ofNullable(CLIENT_CONSUMER_RECORDS.poll());

                    UserRegistrationEvent registrationEvent = (UserRegistrationEvent) response.get().value();
                    assertNotNull(registrationEvent.getUserRegistration().getUserId());
                    assertEquals(getUserDtoWithCorrectCaptcha().getEmail(), registrationEvent.getUserRegistration().getEmail());
                    assertEquals(getUserDtoWithCorrectCaptcha().getFirstName(), registrationEvent.getUserRegistration().getFirstName());
                    assertEquals(getUserDtoWithCorrectCaptcha().getLastName(), registrationEvent.getUserRegistration().getLastName());
                });
    }

    @Test
    @DisplayName("Test send change email event")
    @Sql(scripts = "classpath:db/createUser.sql")
    public void testSendChangeEmailEvent() throws Exception {
        String token = jwtTokenUtils.generateToken(AppUserDetails.build(getAuthorizedUser()));
        post("/api/v1/auth/change-email-link", getUniqueEmailRequest(), "Authorization", "Bearer ".concat(token))
                .andExpect(status().isOk());

        await()
                .pollInterval(Duration.ofSeconds(3))
                .atMost(Duration.ofSeconds(15))
                .untilAsserted(() -> {
                    final var response = Optional.ofNullable(CLIENT_CONSUMER_RECORDS.poll());

                    ChangeEmailEvent changeEmailEvent = (ChangeEmailEvent) response.get().value();
                    assertNotNull(changeEmailEvent.getChangeEmail().getUserId());
                    assertEquals(getUniqueEmailRequest().getEmail(), changeEmailEvent.getChangeEmail().getEmail());
                });
    }

    @NotNull
    private CreateUserDtoRequest getUserDtoWithCorrectCaptcha() {
        return new CreateUserDtoRequest(
                "Alex"
                , "Sergeev"
                , "password"
                , "password"
                , "alex2003@mail.ru"
                , "testCaptcha"
                , "123");
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


    @NotNull
    private EmailRequest getUniqueEmailRequest() {
        return new EmailRequest(
                "alex2003@mail.ru"
        );
    }

    @NotNull
    private CaptchaEntity getCaptchaEntity() {
        return new CaptchaEntity(
                "123"
                , "testCaptcha"
        );
    }
}
