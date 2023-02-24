package com.dev.imageprocessingapi.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {
    public static final String IMAGE_ID_QUEUE = "image_to_convert_queue";
    public static final String IMAGE_EXCHANGE = "image_to_convert_exchange";
    public static final String ROUTING_KEY = "routing-key-fft";

    @Bean
    public Queue queue() {
        return new Queue(IMAGE_ID_QUEUE);
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(IMAGE_EXCHANGE);
    }

    @Bean
    public Binding binding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }
}
