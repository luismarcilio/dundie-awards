package com.ninjaone.dundie_awards;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ninjaone.dundie_awards.adapter.AwardConfigurationQueueSender;
import com.ninjaone.dundie_awards.adapter.AwardEventDto;
import com.ninjaone.dundie_awards.model.Activity;
import com.ninjaone.dundie_awards.model.Organization;
import com.ninjaone.dundie_awards.repository.ActivityRepository;
import com.ninjaone.dundie_awards.repository.OrganizationRepository;
import com.ninjaone.dundie_awards.service.EmployeeService;
import com.ninjaone.dundie_awards.utils.BaseIntegrationTest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class DundieAwardsAsyncTests extends BaseIntegrationTest {

    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private MessageBroker messageBroker;
    @Autowired
    private AwardConfigurationQueueSender sender;
    @Autowired
    private ObjectMapper objectMapper;

    private String dlqMessage;

    @Test
    void testAddAward() throws JsonProcessingException {
        Organization organization = organizationRepository.findAll().get(0);
        assertTrue(employeeService.findAllByOrganization(organization).stream().allMatch(e -> e.getDundieAwards() == null));
        LocalDateTime eventDate = LocalDateTime.now();
        AwardEventDto event = new AwardEventDto(eventDate, organization.getId(), AwardEventDto.Event.AWARD_GIVEN);
        sender.send(event);
        await().atMost(3, TimeUnit.SECONDS)
            .untilAsserted(() -> awardGiven(organization, event));
    }

    private void awardGiven(Organization organization, AwardEventDto event) {
        assertTrue(employeeService.findAllByOrganization(organization).stream().allMatch(e -> e.getDundieAwards() != null && e.getDundieAwards() == 1));
        String eventDescription = String.format("New award given to company %s", organization.getName());
        List<Activity> activities = activityRepository.findAll()
                                        .stream()
                                        .filter(act -> act.getEvent().equals(eventDescription) && act.getOccuredAt().equals(event.occurredAt()))
                                        .toList();
        assertEquals(1, activities.size());
        assertTrue(messageBroker.getMessages().stream().anyMatch(activity -> activity.getOccuredAt().equals(activities.get(0).getOccuredAt())
                                                                                 && activity.getEvent().equals(activities.get(0).getEvent())));
    }

    @Test
    void testAddAwardFails() throws JsonProcessingException {

        dlqMessage = null;
        LocalDateTime eventDate = LocalDateTime.now();
        AwardEventDto event = new AwardEventDto(eventDate, -1L, AwardEventDto.Event.AWARD_GIVEN);
        sender.send(event);
        await().atMost(3, TimeUnit.SECONDS)
            .untilAsserted(() -> awardFail(event));
    }

    private void awardFail(AwardEventDto event) throws JsonProcessingException {
        assertNotNull(dlqMessage, "Message should be sent to DLQ");
        AwardEventDto receivedEvent = objectMapper.readValue(dlqMessage, AwardEventDto.class);
        assertEquals(event, receivedEvent);
    }

    @RabbitListener(queues = "${api.award-notification.dead-letter-queue}")
    public void listenDlQueue(String message) {
        dlqMessage = message;
    }

}
