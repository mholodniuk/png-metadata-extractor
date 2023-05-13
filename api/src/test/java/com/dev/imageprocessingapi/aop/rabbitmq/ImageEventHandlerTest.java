package com.dev.imageprocessingapi.aop.rabbitmq;

import com.dev.imageprocessingapi.aop.ImageEventHandler;
import com.dev.imageprocessingapi.model.Image;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;
import org.springframework.data.mongodb.core.mapping.event.MongoMappingEvent;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;

@SpringJUnitConfig(classes = {ImageEventHandler.class})
public class ImageEventHandlerTest {
    @Autowired
    private ImageEventHandler eventHandler;
    @MockBean
    private RabbitTemplate template;

    private static final String mockId = "644d739d981447205ec6f809";

    @Test
    public void invokeOnAfterSaveEvent() {
        MongoMappingEvent<Image> event = new AfterSaveEvent<>(new Image(), null, "images");
        SampleImageEventListener listener = new SampleImageEventListener();
        listener.onApplicationEvent(event);

        Assertions.assertTrue(listener.invokedOnAfterSave);
    }

    @Test
    public void messageIsSendToRabbitMQ() {
        var image = new Image();
        image.setId(mockId);
        AfterSaveEvent<Image> event = new AfterSaveEvent<>(image, null, "images");
        eventHandler.onAfterSave(event);

        then(template).should().convertAndSend(any(), any(), eq(image.getId()));
    }

    static class SampleImageEventListener extends AbstractMongoEventListener<Image> {
        boolean invokedOnAfterSave;

        @Override
        public void onAfterSave(AfterSaveEvent<Image> event) {
            invokedOnAfterSave = true;
        }
    }
}
