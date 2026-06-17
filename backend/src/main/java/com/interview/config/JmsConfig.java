package com.interview.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

/**
 * JMS configuration for internal messaging.
 *
 * Configures embedded ActiveMQ broker with queues for:
 * - artist.queue - Artist entity change notifications
 * - song.queue - Song entity change notifications
 * - album.queue - Album entity change notifications
 */
@Configuration
public class JmsConfig {

    /**
     * Configure embedded ActiveMQ connection factory.
     * Uses VM transport for in-process messaging.
     *
     * The create=true parameter ensures the broker is created on first connection.
     * The waitForStart parameter ensures the connection waits for broker startup.
     */
    @Bean
    public ActiveMQConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL("vm://localhost?broker.persistent=false&create=true&waitForStart=10000");
        connectionFactory.setTrustAllPackages(true);
        return connectionFactory;
    }

    /**
     * Configure JMS template for sending messages.
     *
     * sessionTransacted is set to false to ensure messages are sent immediately
     * without waiting for a transaction commit. This is important for async
     * notification delivery.
     */
    @Bean
    public JmsTemplate jmsTemplate() {
        JmsTemplate template = new JmsTemplate();
        template.setConnectionFactory(connectionFactory());
        template.setMessageConverter(jacksonJmsMessageConverter());
        template.setSessionTransacted(false);
        return template;
    }

    /**
     * Configure JMS listener container factory for receiving messages.
     */
    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        factory.setMessageConverter(jacksonJmsMessageConverter());
        return factory;
    }

    /**
     * Configure Jackson message converter for JSON serialization.
     *
     * Registers JavaTimeModule to support LocalDateTime serialization in
     * NotificationMessage timestamps.
     */
    @Bean
    public MessageConverter jacksonJmsMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");

        // Register JavaTimeModule for LocalDateTime support
        com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
        objectMapper.findAndRegisterModules();
        converter.setObjectMapper(objectMapper);

        return converter;
    }
}
