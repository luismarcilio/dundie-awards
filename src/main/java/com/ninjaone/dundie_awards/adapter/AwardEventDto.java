package com.ninjaone.dundie_awards.adapter;

import java.time.LocalDateTime;

public record AwardEventDto(
    LocalDateTime occurredAt,
    Long organizationId,
    Event event
) {
    public enum Event {
        AWARD_GIVEN
    }
}
