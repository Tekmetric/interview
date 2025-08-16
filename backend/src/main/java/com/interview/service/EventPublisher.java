package com.interview.service;

import com.interview.dto.EmailEventDto;
import com.interview.entity.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;

// Publishes domain events (like "customer created") to RabbitMQ
@Service
@RequiredArgsConstructor
public class EventPublisher {
    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbitmq.exchange.customer}")
    private String exchange;

    @Value("${app.rabbitmq.routing-key.email}")
    private String emailRoutingKey;

    public void publishCustomerCreatedEvent(Customer customer) {
        EmailEventDto event = EmailEventDto.builder()
                .email(customer.getEmail())
                .eventType("CUSTOMER_CREATED")
                .additionalData(new HashMap<String, Object>() {{
                        put("customerId", customer.getId());
                        put("firstName", customer.getFirstName());
                        put("lastName", customer.getLastName());
                        put("createdAt", customer.getCreatedAt());
                }})
                .build();

        try {
            rabbitTemplate.convertAndSend(exchange, emailRoutingKey, event);
            System.out.println("Published customer created event for: " + customer.getEmail());
        } catch (Exception e) {
            System.err.println("Failed to publish customer event: " + e.getMessage());
        }
    }
}