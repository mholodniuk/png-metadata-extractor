package com.dev.imageprocessingapi.event;

import com.dev.imageprocessingapi.model.Image;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ImageEventHandler extends AbstractMongoEventListener<Image> {
    @Override
    public void onAfterSave(AfterSaveEvent<Image> event) {
        log.info("Inserting Image to the database");
        var image = event.getSource();
        log.info(image.toString());
    }
}