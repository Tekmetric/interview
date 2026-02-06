package com.interview.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket configuration for real-time notifications.
 *
 * Enables STOMP protocol over WebSocket for client subscriptions to:
 * - /topic/artists - Artist entity changes
 * - /topic/songs - Song entity changes
 * - /topic/albums - Album entity changes
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enable a simple in-memory message broker to send messages to clients
        // on destinations prefixed with "/topic"
        config.enableSimpleBroker("/topic");

        // Prefix for messages from clients (not used in this implementation)
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Register the "/ws" endpoint for WebSocket connections
        // SockJS fallback option is enabled for browsers that don't support WebSocket
        registry.addEndpoint("/ws")
                .setAllowedOrigins("*")
                .withSockJS();
    }
}
