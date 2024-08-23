package com.ninjaone.dundie_awards.utils;

import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public class Containers {
    @Container
    public static final RabbitMQContainer rabbit;

    static {
        rabbit = new RabbitMQContainer(DockerImageName.parse("rabbitmq:3-management"));
    }
}
