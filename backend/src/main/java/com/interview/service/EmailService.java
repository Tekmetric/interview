package com.interview.service;

import com.interview.dto.EmailEventDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {
    @RabbitListener(queues = "${app.rabbitmq.queue.customer-email}")
    public void sendEmail(EmailEventDto event) {
        log.info("Received {} event for {} with details {}", event.getEventType(), event.getEmail(), event.getAdditionalData());
        // logic to build email subject/content based on eventType and additional data,
        // and then send the email
        log.info("Sending email to {}", event.getEmail());
    }
}