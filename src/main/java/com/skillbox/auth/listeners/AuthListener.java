package com.skillbox.auth.listeners;

import com.skillbox.auth.dto.events.AccountChangesEvent;
import com.skillbox.auth.entity.UserEntity;
import com.skillbox.auth.service.UserService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@ConditionalOnProperty(prefix = "app.kafka", name = "enable", havingValue = "true")
public class AuthListener {
    private final UserService userService;

    public AuthListener(UserService userService) {
        this.userService = userService;
    }


    @KafkaListener(topics = {"${app.kafka.accountChange}"}
            , groupId = "${app.kafka.kafkaMessageGroupId}"
            , containerFactory = "concurrentKafkaListenerContainerFactory")
    public void accountChangeConsumer(@Payload AccountChangesEvent message,
                                        @Header(value = KafkaHeaders.RECEIVED_TOPIC) String topic
    ) {
        if (message.getAccountChanges().getFirstName() == null ||
                message.getAccountChanges().getLastName() == null ||
                message.getAccountChanges().getId() == null) return;
        UserEntity user = userService.findUserById(message.getAccountChanges().getId());
        user.setFirstName(message.getAccountChanges().getFirstName());
        user.setLastName(message.getAccountChanges().getLastName());
        userService.saveUser(user);

    }
}
