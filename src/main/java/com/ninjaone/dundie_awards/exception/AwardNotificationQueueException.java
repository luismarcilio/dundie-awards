package com.ninjaone.dundie_awards.exception;

public class AwardNotificationQueueException extends RuntimeException {
    public AwardNotificationQueueException(Exception other) {
        super(other);
    }
}
