package com.dev.imageprocessingapi.infrastructure.event;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class ExtractionTrackAspect {
    @Around("@annotation(com.dev.imageprocessingapi.infrastructure.event.annotation.TrackExecutionTime)")
    public Object executionTime(ProceedingJoinPoint point) throws Throwable {
        long startTime = System.nanoTime();
        Object object = point.proceed();
        long endTime = System.nanoTime();
        log.info("Time taken for Execution is: " + (endTime - startTime) / 1000 + " microseconds. " +
                "Method: " + point.getSignature().getName());
        return object;
    }
}
