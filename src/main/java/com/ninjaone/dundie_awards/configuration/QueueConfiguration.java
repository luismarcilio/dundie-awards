package com.ninjaone.dundie_awards.configuration;

import com.ninjaone.dundie_awards.adapter.AwardNotificationQueueReceiver;
import com.rabbitmq.client.Channel;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueueConfiguration {

    private static final Logger log = LoggerFactory.getLogger(QueueConfiguration.class);
    private final AwardNotificationQueueReceiver awardNotificationQueueReceiver;

    @Value("${api.award-notification.queue-name}")
    private String awardNotificationQueueName;

    @Value("${api.award-notification.dead-letter-queue}")
    private String awardNotificationDeadLetterQueue;

    @Value("${api.award-notification.dead-letter-exchange}")
    private String awardNotificationDeadLetterExchange;

    @Value("${api.award-notification.dead-letter-routing-key}")
    private String awardNotificationDeadLetterRoutingKey;

    public QueueConfiguration(AwardNotificationQueueReceiver awardNotificationQueueReceiver) {
        this.awardNotificationQueueReceiver = awardNotificationQueueReceiver;
    }

    @Bean
    public Queue awardNotificationQueue() {
        return QueueBuilder.durable(awardNotificationQueueName)
                   .deadLetterExchange(awardNotificationDeadLetterExchange)
                   .deadLetterRoutingKey(awardNotificationDeadLetterRoutingKey)
                   .build();

    }

    @Bean
    public Queue awardNotificationDlQueue() {
        return QueueBuilder.durable(awardNotificationDeadLetterQueue)
                   .build();
    }

    @Bean
    TopicExchange deadLetterExchange() {
        return new TopicExchange(awardNotificationDeadLetterExchange);
    }

    @Bean
    Binding deadLetterBinding() {
        return BindingBuilder
                   .bind(awardNotificationDlQueue())
                   .to(deadLetterExchange())
                   .with(awardNotificationDeadLetterRoutingKey);

    }

    @RabbitListener(queues = "${api.award-notification.queue-name}")
    public void listen(Message message, Channel channel) throws IOException {
        try {
            awardNotificationQueueReceiver.accept(new String(message.getBody()));
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("Error processing message", e);
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
        }
    }
}
