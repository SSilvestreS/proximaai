package com.proximaai.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Habilita o broker de mensagens para envio de mensagens para clientes
        config.enableSimpleBroker("/topic", "/queue", "/user");
        
        // Prefixo para mensagens enviadas pelo cliente
        config.setApplicationDestinationPrefixes("/app");
        
        // Prefixo para mensagens privadas
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint para conexão WebSocket
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // Em produção, especificar domínios específicos
                .withSockJS(); // Fallback para navegadores que não suportam WebSocket
    }
}
