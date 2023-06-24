package com.dev.imageprocessingapi.metadataextractor.aop;


import com.dev.imageprocessingapi.metadataextractor.model.RawChunk;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.dev.imageprocessingapi.metadataextractor.utils.ConversionUtils.calculateCRC;

@Slf4j
@Aspect
@Component
@EnableAspectJAutoProxy
public class ImageModificationAspect {
    @Around("execution(* com.dev.imageprocessingapi.metadataextractor.ImageSerializer.saveAsPNG(..))")
    public Object updateLastModifiedMetadata(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        int index = 0;
        Object[] modifiedArgs = proceedingJoinPoint.getArgs();
        List<RawChunk> modifiedChunks = new ArrayList<>();

        for (Object arg : modifiedArgs) {
            if (arg instanceof List<?> chunks) {
                for (Object object : chunks) {
                    RawChunk chunk = (RawChunk) object;

                    if (chunk.type().equals("tIME")) {
                        LocalDateTime currentDate = LocalDateTime.now();
                        log.info("Found tIME chunk. Changing last modified to: " + currentDate);
                        byte[] modificationDateBytes = convertCurrentDateToBytes(currentDate);
                        String crc = calculateCRC(modificationDateBytes, chunk.type());
                        chunk = new RawChunk(chunk.type(), chunk.length(), chunk.offset(), modificationDateBytes, crc);
                    }
                    modifiedChunks.add(chunk);
                }
                // todo: add tIME chunk even if there wasn't one before
                modifiedArgs[index] = modifiedChunks;
            }
            index++;
        }
        return proceedingJoinPoint.proceed(modifiedArgs);
    }

    private static byte[] convertCurrentDateToBytes(LocalDateTime now) {
        byte[] yearBytes = new byte[]{(byte) (now.getYear() >> 8), (byte) (now.getYear())};
        return new byte[]{yearBytes[0], yearBytes[1], (byte) now.getMonthValue(), (byte) now.getDayOfMonth(), (byte) now.getHour(), (byte) now.getMinute(), (byte) now.getSecond()};
    }
}
