package com.petg.blois.service;

import com.fasterxml.jackson.databind.JavaType;
import lombok.Builder;
import lombok.Data;

import java.time.format.DateTimeFormatter;

/**
 * JsonOptions
 *
 * @author F. LUTZ (97211P)
 */
@Data
@Builder(toBuilder = true)
public class JsonOptions {
    private JavaType javaType;
    @Builder.Default
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
    @Builder.Default
    private DateTimeFormatter timeFormatter = DateTimeFormatter.ISO_LOCAL_TIME;
    @Builder.Default
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    @Builder.Default
    private DateTimeFormatter zonedDateTimeFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;
}
