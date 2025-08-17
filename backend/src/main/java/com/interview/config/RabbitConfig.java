package com.interview.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitConfig {
    @Value("${app.rabbitmq.queue.customer-email}")
    private String emailQueue;

    @Value("${app.rabbitmq.exchange.customer}")
    private String exchange;

    @Value("${app.rabbitmq.routing-key.email}")
    private String emailRoutingKey;

    @Bean
    public Queue customerEmailQueue() {
        return QueueBuilder.durable(emailQueue).build();
    }

    @Bean
    public TopicExchange customerExchange() {
        return new TopicExchange(exchange);
    }

    @Bean
    public Binding emailBinding() {
        return BindingBuilder
                .bind(customerEmailQueue())
                .to(customerExchange())
                .with(emailRoutingKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}