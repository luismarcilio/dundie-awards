package com.ninjaone.dundie_awards.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class AwardConfigurationQueueSender {
    private final RabbitTemplate rabbitTemplate;
    private final Queue awardNotificationQueue;
    private final ObjectMapper objectMapper;

    public AwardConfigurationQueueSender(RabbitTemplate rabbitTemplate, Queue awardNotificationQueue, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.awardNotificationQueue = awardNotificationQueue;
        this.objectMapper = objectMapper;
    }

    public void send(AwardEventDto message) throws JsonProcessingException {
        String messageAsJson = objectMapper.writeValueAsString(message);
        rabbitTemplate.convertAndSend(awardNotificationQueue.getName(), messageAsJson);
    }
}
