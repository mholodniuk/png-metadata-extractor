package com.dev.imageprocessingapi.aop;


import com.dev.imageprocessingapi.metadataextractor.model.RawChunk;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Slf4j
@Aspect
@Component
@EnableAspectJAutoProxy
public class ImageModificationAspect {
    @Around("execution(* com.dev.imageprocessingapi.metadataextractor.logic.ImageSerializer..*(..))")
    public Object updateLastModifiedMetadata(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        int index = 0;
        Object[] modifiedArgs = proceedingJoinPoint.getArgs();

        for (Object arg : modifiedArgs) {
            if (arg instanceof List<?> chunks) {
                for (Object object : chunks) {
                    RawChunk chunk = (RawChunk) object;
//                    byte[] bytes = ConversionUtils.parseHexString(String.join("", chunk.rawBytes()));
//                    long crc = CRC.calculateCRC(CRC.Parameters.CRC32, bytes); // does not match ???
                    if (chunk.type().equals("tIME")) {
                        log.info("Found tIME chunk. Changing its last modified to NOW");
                        log.info(String.valueOf(Instant.now()));
                    }
                }
                modifiedArgs[index] = arg;
            }
            index++;
        }
        return proceedingJoinPoint.proceed(modifiedArgs);
    }
}
