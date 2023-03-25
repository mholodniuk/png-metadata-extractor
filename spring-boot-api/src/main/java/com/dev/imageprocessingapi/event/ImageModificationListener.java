package com.dev.imageprocessingapi.event;


import com.dev.imageprocessingapi.metadataextractor.model.Chunk;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Aspect
@Component
@EnableAspectJAutoProxy
public class ImageModificationListener {
    @Around("execution(* com.dev.imageprocessingapi.metadataextractor.logic.ImageSerializer..*(..))")
    public Object updateLastModifiedMetadata(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        int index = 0;
        Object[] modifiedArgs = proceedingJoinPoint.getArgs();

        for (Object arg : proceedingJoinPoint.getArgs()) {
            if (arg instanceof List<?> chunks) {
                for (Object object : chunks) {
                    Chunk chunk = (Chunk) object;
                    if (chunk.type().equals("tIME")) {
                        log.info("Found tIME chunk. Changing its last modified to NOW");
                        // todo: now rewrite Instant.now() to bytes
                    }
                }
                modifiedArgs[index] = arg;
            }
            index++;
        }
        return proceedingJoinPoint.proceed(modifiedArgs);
    }
}
