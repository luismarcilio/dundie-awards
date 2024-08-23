package com.ninjaone.dundie_awards.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ninjaone.dundie_awards.exception.AwardNotificationQueueException;
import com.ninjaone.dundie_awards.service.EmployeeService;
import java.util.function.Consumer;
import org.springframework.stereotype.Component;

@Component
public class AwardNotificationQueueReceiver implements Consumer<String> {
    private final ObjectMapper objectMapper;
    private final EmployeeService employeeService;

    public AwardNotificationQueueReceiver(ObjectMapper objectMapper, EmployeeService employeeService) {
        this.objectMapper = objectMapper;
        this.employeeService = employeeService;
    }

    @Override
    public void accept(String s) {
        try {
            AwardEventDto message = objectMapper.readValue(s, AwardEventDto.class);
            switch (message.event()) {
                case AWARD_GIVEN -> this.employeeService.addAward(message.organizationId(), message.occurredAt());
                default -> throw new UnsupportedOperationException();
            }
        } catch (JsonProcessingException e) {
            throw new AwardNotificationQueueException(e);
        }
    }

}
