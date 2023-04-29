package com.dev.imageprocessingapi.aop;


import com.dev.imageprocessingapi.metadataextractor.model.RawChunk;
import com.dev.imageprocessingapi.metadataextractor.utils.ConversionUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.CRC32;

@Slf4j
@Aspect
@Component
@EnableAspectJAutoProxy
public class ImageModificationAspect {
    @Around("execution(* com.dev.imageprocessingapi.metadataextractor.logic.ImageSerializer.saveAsPNG(..))")
    public Object updateLastModifiedMetadata(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        int index = 0;
        Object[] modifiedArgs = proceedingJoinPoint.getArgs();
        List<RawChunk> modifiedChunks = new ArrayList<>();

        for (Object arg : modifiedArgs) {
            if (arg instanceof List<?> chunks) {
                for (Object object : chunks) {
                    RawChunk chunk = (RawChunk) object;
                    String crc = recalculateCRC(chunk.rawBytes(), chunk.type());
                    log.info("chunk " + chunk.type() + " crc: " + crc);
                    if (chunk.type().equals("tIME")) {
                        LocalDateTime currentDate = LocalDateTime.now();
                        log.info("Found tIME chunk. Changing last modified to: " + currentDate);
                        List<String> modificationDateBytes = convertCurrentDateToBytes(currentDate);
                        chunk = new RawChunk(chunk.type(), chunk.length(), chunk.offset(), modificationDateBytes, crc);
                    }
                    modifiedChunks.add(chunk);
                }
                modifiedArgs[index] = modifiedChunks;
            }
            index++;
        }
        return proceedingJoinPoint.proceed(modifiedArgs);
    }

    private static String recalculateCRC(List<String> bytes, String type) {
        CRC32 crc = new CRC32();
        crc.update(ConversionUtils.parseHexString(type + String.join("", bytes)));
        return Long.toHexString(crc.getValue());
    }

    private static List<String> convertCurrentDateToBytes(LocalDateTime now) {
        byte[] yearBytes = new byte[]{(byte) (now.getYear() >> 8), (byte) (now.getYear())};
        String year = ConversionUtils.formatHex(yearBytes);
        String month = ConversionUtils.toHexDigits((byte) now.getMonthValue());
        String day = ConversionUtils.toHexDigits((byte) now.getDayOfMonth());
        String hour = ConversionUtils.toHexDigits((byte) now.getHour());
        String minute = ConversionUtils.toHexDigits((byte) now.getMinute());
        String second = ConversionUtils.toHexDigits((byte) now.getSecond());
        return List.of(year.substring(0, 2), year.substring(2), month, day, hour, minute, second);
    }
}
