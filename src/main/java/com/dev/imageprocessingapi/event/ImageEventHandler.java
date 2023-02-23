package com.dev.imageprocessingapi.event;

import com.dev.imageprocessingapi.config.RabbitConfiguration;
import com.dev.imageprocessingapi.model.Image;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImageEventHandler extends AbstractMongoEventListener<Image> {
    private final RabbitTemplate template;
    @Override
    public void onAfterSave(AfterSaveEvent<Image> event) {
        var image = event.getSource();
        log.info("Adding Image ID to the fourier transform generator..." + image.getId());
        template.convertAndSend(RabbitConfiguration.IMAGE_EXCHANGE, RabbitConfiguration.ROUTING_KEY, image.getId());
    }
}