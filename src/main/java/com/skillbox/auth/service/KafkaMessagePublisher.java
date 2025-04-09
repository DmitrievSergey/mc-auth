package com.skillbox.auth.service;


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

    @Value("${app.kafka.resetPassword}")
    private String resetPasswordTopic;

    private final KafkaTemplate<String, UserRegistrationEvent> template1;
    private final KafkaTemplate<String, ResetPasswordEvent> template2;

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

    public void sendResetPasswordEvent(ResetPasswordEvent event) {
        CompletableFuture<SendResult<String, ResetPasswordEvent>> result = template2.send(resetPasswordTopic, event);

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
