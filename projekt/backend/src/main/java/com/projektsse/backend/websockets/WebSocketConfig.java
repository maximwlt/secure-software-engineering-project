package com.projektsse.backend.websockets;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuration for websocket communication using STOMP protocol.
 * It defines the endpoint for websocket connections and configures the message broker for handling messages.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Registers the STOMP endpoint for websocket connections. The endpoint is set to "/ws" and allows all origins.
     * @param registry the stomp registry to add the endpoint to
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*");
    }

    /**
     * Handles the message routing and activating an in-memory broker to handle message subscriptions
     * @param registry the message broker registry to configure the message broker
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app"); // Broker destinations starting with "/app" will be routed to message-handling methods
        registry.enableSimpleBroker("/topic", "/queue");// .setHeartbeatValue(new long[]{10000, 20000}); // Enable a simple in-memory broker for destinations starting with "/topic" and set heartbeat intervals
    }

}
