package com.petg.blois.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * @author F. LUTZ (97211P)
 */
public enum ReflectionClass {
    BOOLEAN(boolean.class, Boolean.class),
    BYTE(byte.class, Byte.class),
    SHORT(short.class, Short.class),
    INTEGER(int.class, Integer.class),
    LONG(long.class, Long.class),
    FLOAT(float.class, Float.class),
    DOUBLE(double.class, Double.class),
    CHARACTER(char.class, Character.class),
    STRING(String.class),
    LOCAL_TIME(LocalTime.class),
    LOCAL_DATE(LocalDate.class),
    LOCAL_DATE_TIME(LocalDateTime.class),
    ZONED_DATE_TIME(ZonedDateTime.class),
    ENUM(Object.class),
    RECORD(Object.class),
    ARRAY(Object.class),
    COLLECTION(Object.class),
    MAP(Object.class),
    BEAN(Object.class);

    private final List<Class<?>> classes;

    ReflectionClass(Class<?>... classes) {
        this.classes = List.of(classes);
    }

    public static ReflectionClass of(Class<?> clazz) {
        Optional<ReflectionClass> opt = Arrays.stream(ReflectionClass.values())
                .filter(reflectionClass -> reflectionClass.classes.contains(clazz))
                .findFirst();
        if (opt.isPresent()) {
            return opt.get();
        } else if (clazz.isEnum()) {
            return ENUM;
        } else if (clazz.isRecord()) {
            return RECORD;
        } else if (clazz.isArray()) {
            return ARRAY;
        } else if (Collection.class.isAssignableFrom(clazz)) {
            return COLLECTION;
        } else if (Map.class.isAssignableFrom(clazz)) {
            return MAP;
        } else {
            return BEAN;
        }
    }
}
