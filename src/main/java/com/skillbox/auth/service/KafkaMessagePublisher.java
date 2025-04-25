package com.skillbox.auth.service;


import com.skillbox.auth.dto.events.ChangeEmailEvent;
import com.skillbox.auth.dto.events.ResetPasswordEvent;
import com.skillbox.auth.dto.events.UserRegistrationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;


@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaMessagePublisher {
    @Value("${app.kafka.userRegistration}")
    private String userRegistrationTopic;

    @Value("${app.kafka.emailChange}")
    private String changeEmailTopic;

    private final KafkaTemplate<String, UserRegistrationEvent> template1;
    private final KafkaTemplate<String, ChangeEmailEvent> template2;

    public void sendUserRegistrationEvent(UserRegistrationEvent event) {
        CompletableFuture<SendResult<String, UserRegistrationEvent>> result = template1.send(userRegistrationTopic, event);

        result.whenComplete((res, ex) -> {
            if(ex == null) {
                log.info("Sent event {}", event);
                log.info("with offset {}", res.getProducerRecord().toString());
            } else {
                log.info("Unable sent event {}", event);
                log.info("Error {}", ex.getLocalizedMessage());
            }
        });
    }

    public void sendChangeEmailEvent(ChangeEmailEvent event) {
        CompletableFuture<SendResult<String, ChangeEmailEvent>> result = template2.send(changeEmailTopic, event);

        result.whenComplete((res, ex) -> {
            if(ex == null) {
                log.info("Sent event {}", event);
                log.info("with offset {}", res.getProducerRecord().toString());
            } else {
                log.info("Unable sent event {}", event);
                log.info("Error {}", ex.getLocalizedMessage());
            }
        });
    }
}
