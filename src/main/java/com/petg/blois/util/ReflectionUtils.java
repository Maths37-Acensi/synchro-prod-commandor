package com.petg.blois.util;

import com.petg.blois.exception.ServiceException;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils;
import org.springframework.lang.Nullable;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.lang.reflect.*;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * ReflectionService
 *
 * @author F. LUTZ (97211P)
 */
@Slf4j
@UtilityClass
public class ReflectionUtils {
    /**
     * Create new object from default constructor with no args.
     *
     * @param clazz object to create
     * @param <T>   generic type of clazz
     * @return new object T
     */
    @SuppressWarnings("unchecked")
    public <T, U> Optional<? extends T> findAndCallConstructor(Class<T> clazz, U... args) {
        List<Class<U>> parameterTypes = Arrays.stream(args)
                .map(object -> (Class<U>) object.getClass())
                .toList();
        return Arrays.stream(clazz.getDeclaredConstructors())
                .filter(constructor -> {
                    List<Class<U>> currentParameterTypes = Arrays.stream(constructor.getParameterTypes())
                            .map(parameterType -> (Class<U>) parameterType)
                            .toList();
                    return PGUtils.isEqualsCollections(currentParameterTypes, parameterTypes, true);
                })
                .findFirst()
                .map(constructor -> {
                    try {
                        return (T) constructor.newInstance((Object[]) args);
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                        throw new ServiceException(e);
                    }
                });
    }

    /**
     * Create new object from default constructor with no args.
     *
     * @param clazz object to create
     * @param <T>   generic type of clazz
     * @return new object T
     */
    @SuppressWarnings("unchecked")
    public <T, P> Optional<? extends T> findAndCallConstructorWithDefinedTypes(Class<T> clazz, List<P> parameters, List<Class<P>> parameterTypes) {
        return Arrays.stream(clazz.getDeclaredConstructors())
                .filter(constructor -> {
                    List<Class<P>> currentParameterTypes = Arrays.stream(constructor.getParameterTypes())
                            .map(parameterType -> (Class<P>) parameterType)
                            .toList();
                    return PGUtils.isEqualsCollections(currentParameterTypes, parameterTypes, true);
                })
                .findFirst()
                .map(constructor -> {
                    try {
                        return (T) constructor.newInstance(parameters.toArray());
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                        throw new ServiceException(e);
                    }
                });
    }

    public <T> List<com.petg.blois.util.ReflectionPropertyDescriptor> findAllPropertyDescriptors(Class<T> beanClass) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);
            return findAllField(beanClass).stream()
                    .filter(field -> !Modifier.isStatic(field.getModifiers()))
                    .map(field -> {
                        ReflectionPropertyDescriptor descriptor = new ReflectionPropertyDescriptor();
                        descriptor.setField(field);

                        if (beanClass.isRecord()) {
                            Arrays.stream(beanInfo.getMethodDescriptors())
                                    .filter(currentDescriptor -> currentDescriptor.getName().equals(field.getName()))
                                    .findFirst()
                                    .ifPresent(methodDescriptor -> descriptor.setReadMethod(methodDescriptor.getMethod()));
                        } else {
                            Arrays.stream(beanInfo.getPropertyDescriptors())
                                    .filter(currentDescriptor -> currentDescriptor.getName().equals(field.getName()))
                                    .findFirst()
                                    .ifPresent(propertyDescriptor -> {
                                        descriptor.setReadMethod(propertyDescriptor.getReadMethod());
                                        descriptor.setWriteMethod(propertyDescriptor.getWriteMethod());
                                    });
                        }
                        return descriptor;
                    })
                    .filter(descriptor -> descriptor.getReadMethod() != null || descriptor.getWriteMethod() != null)
                    .toList();
        } catch (IntrospectionException e) {
            throw new ServiceException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Class<?>> findAllParameterizedTypes(Field field) {
        List<Class<?>> parameterizedTypeList = new ArrayList<>();
        if (field.getGenericType() instanceof ParameterizedType parameterizedType) {
            parameterizedTypeList = Arrays.asList(Arrays.stream(parameterizedType.getActualTypeArguments())
                    .map(type -> (Class<?>) type)
                    .toArray(Class[]::new)
            );
        }
        return parameterizedTypeList;
    }

    /**
     * Return all fields of class T (fields of class T and its super classes
     *
     * @param clazz           Class to get all fields
     * @param includeSuperOpt Default true. If true, add super class fields.
     * @param <T>             Generic type of clazz
     * @return List of Field
     */
    public <T> List<Field> findAllField(Class<T> clazz, Boolean... includeSuperOpt) {
        List<Field> fields = new ArrayList<>();
        Class<?> currentClass = clazz;
        boolean includeSuper = PGUtils.findFirstParameter(includeSuperOpt)
                .orElse(true);

        if (includeSuper) {
            while (currentClass != null) {
                fields.addAll(Arrays.asList(currentClass.getDeclaredFields()));

                currentClass = currentClass.getSuperclass();
            }
        } else {
            fields.addAll(Arrays.asList(currentClass.getDeclaredFields()));
        }

        return fields;
    }

    /**
     * Invoke class method to set or get value
     *
     * @param m    method to call
     * @param bean object that contains the method
     * @param args value to set
     * @param <T>  generic type of clazz
     * @return object return by invoke method
     */
    public <T> Optional<?> invoke(Method m, T bean, Object... args) {
        try {
            return Optional.ofNullable(m.invoke(bean, args));
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Find and invoke class method getter
     *
     * @param descriptor PropertyDescriptor of field
     * @param bean       Object to reflect
     * @param <T>        generic type of clazz
     * @return object return by invoke method
     */
    @SuppressWarnings("unchecked")
    public <T, U> Optional<U> readField(ReflectionPropertyDescriptor descriptor, T bean) {
        return (Optional<U>) invoke(descriptor.getReadMethod(), bean);
    }

    /**
     * Find and invoke class method setter
     *
     * @param descriptor PropertyDescriptor of field
     * @param bean       Object to reflect
     * @param value      Value to set
     * @param <T>        generic type of clazz
     */
    public <T, U> void writeField(ReflectionPropertyDescriptor descriptor, T bean, @Nullable U value) {
        if (value != null) {
            invoke(descriptor.getWriteMethod(), bean, value);
        }
    }

    /**
     * Réalise une copie d'un objet.
     *
     * @param source Objet à copier.
     * @param <T>    generic type of clazz
     * @return Retourne une copie de source.
     */
    @SuppressWarnings("unchecked")
    public <T, U> T copy(@NonNull T source, U... args) {
        Class<T> clazz = (Class<T>) source.getClass();
        T t = findAndCallConstructor(clazz, args)
                .orElseThrow(() -> new ServiceException("Missing constructor for class " + source.getClass()));
        findAllPropertyDescriptors(clazz).stream()
                .filter(descriptor -> descriptor.getReadMethod() != null && descriptor.getWriteMethod() != null)
                .forEach(descriptor -> {
                    Object value = readField(descriptor, source)
                            .orElse(null);
                    writeField(descriptor, t, value);
                });
        return t;
    }

    /**
     * Parcours les attributs d'une classe et remplace ceux non renseigné par ceux de l'objet par défaut.
     *
     * @param sourceValue  Objet source sur lequel on ajoutera les valeurs par défaut.
     * @param defaultValue Objet contenant les valeurs par défaut.
     * @param <T>          generic type of clazz
     * @return Retourne une copie de sourceValue avec les valeurs par défaut.
     */
    @SuppressWarnings("unchecked")
    public <T> T defaultIfEmpty(@Nullable T sourceValue, @NonNull T defaultValue) {
        T buildingValue = PGUtils.defaultIfEmpty(sourceValue, defaultValue);
        Class<T> clazz = (Class<T>) buildingValue.getClass();

        findAllPropertyDescriptors(clazz).stream()
                .filter(descriptor -> descriptor.getReadMethod() != null && descriptor.getWriteMethod() != null)
                .forEach(descriptor -> {
                    Object sourceAtt = readField(descriptor, buildingValue)
                            .orElse(null);
                    Object defaultAtt = readField(descriptor, defaultValue)
                            .orElse(null);
                    if (defaultAtt != null) {
                        Object value = PGUtils.defaultIfEmpty(sourceAtt, defaultAtt);
                        writeField(descriptor, buildingValue, value);
                    }
                });

        return buildingValue;
    }

    /**
     * Cast o to a specific type
     *
     * @param o     Object to cast
     * @param clazz Target class of o
     * @param <T>   generic type of clazz
     * @return o cast in T
     */
    @SuppressWarnings("unchecked")
    public <T> @Nullable T cast(@Nullable Object o, Class<T> clazz) {
        if (o == null) {
            return null;
        }
        Class<?> c1 = ClassUtils.primitiveToWrapper(o.getClass());
        Class<T> c2 = (Class<T>) ClassUtils.primitiveToWrapper(clazz);
        T t;
        try {
            if (c1.equals(c2)) {
                // Same class
                t = c2.cast(o);
            } else if (Number.class.isAssignableFrom(c1) && Number.class.isAssignableFrom(c2)) {
                // Particular case : Numbers
                t = castNumber((Number) o, c2);
            } else if (String.class.equals(c1) && Character.class.equals(c2) && ((String) o).length() == 1) {
                // Particular case : Characters / String
                t = (T) ((Object) ((String) o).charAt(0));
            } else if (String.class.equals(c2) && Character.class.equals(c1)) {
                // Particular case : Characters / String
                t = (T) ((Character) o).toString();
            } else if (LocalDate.class.equals(c2)) {
                // Particular case : Date
                java.sql.Date date = (java.sql.Date) o;
                t = c2.cast(date.toLocalDate());
            } else if (LocalTime.class.equals(c2)) {
                // Particular case : Time
                Time time = (Time) o;
                t = c2.cast(time.toLocalTime());
            } else if (LocalDateTime.class.equals(c2)) {
                // Particular case : Timestamp
                Timestamp timestamp = (Timestamp) o;
                t = c2.cast(timestamp.toLocalDateTime());
            } else if (c2.isEnum()) {
                // Particular case : Enum
                t = castEnum(o, c2);
            } else if (Number.class.isAssignableFrom(c1) && Boolean.class.equals(c2)) {
                int i = ((Number) o).intValue();
                t = c2.cast(i == 1);
            } else {
                // Default
                t = c2.cast(o);
            }
        } catch (ClassCastException e) {
            throw new ServiceException(e);
        }

        return t;
    }

    /**
     * Méthode permettant de transformer un number vers un type spécifique de number.
     *
     * @param source Object à caster
     * @param clazz  Classe cible, doit être un Number
     * @param <T>    Type générique
     * @return Un number
     */
    private <T> T castNumber(Number source, Class<T> clazz) {
        T t;

        if (clazz.getTypeName().equals(Byte.class.getTypeName())) {
            t = clazz.cast((source).byteValue());
        } else if (clazz.getTypeName().equals(Short.class.getTypeName())) {
            t = clazz.cast((source).shortValue());
        } else if (clazz.getTypeName().equals(Integer.class.getTypeName())) {
            t = clazz.cast((source).intValue());
        } else if (clazz.getTypeName().equals(Long.class.getTypeName())) {
            t = clazz.cast((source).longValue());
        } else if (clazz.getTypeName().equals(Float.class.getTypeName())) {
            t = clazz.cast((source).floatValue());
        } else {
            t = clazz.cast((source).doubleValue());
        }

        return t;
    }

    /**
     * Méthode permettant de caster une valeur en enum.
     * On regarde si l'enum contient la méthode of, si oui on l'appelle, sinon on map par rapport au name (en ignorant la casse).
     *
     * @param o     Object à caster
     * @param clazz Classe cible
     * @param <T>   Type générique
     * @return Un enum
     */
    private <T> T castEnum(Object o, Class<T> clazz) {
        Method of;
        try {
            of = clazz.getMethod("of", String.class);
            return clazz.cast(of.invoke(o, o.toString()));
        } catch (NoSuchMethodException e) {
            log.trace(e.getMessage());
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new ServiceException(e);
        }

        return Arrays.stream(clazz.getEnumConstants()).filter(e -> e.toString().equalsIgnoreCase(o.toString()))
                .findFirst()
                .orElseThrow(() -> new ServiceException("Missing value in enum"));
    }

    /**
     * Parse string s to a specific type
     *
     * @param s             String to parse
     * @param clazz         Target class of o
     * @param formattersOpt for types LocalDate, LocalTime and LocalDateTime, if not classic formattersOpt
     * @param <T>           generic type of clazz
     * @return s parse in T
     */
    public <T> Optional<T> parse(@Nullable String s, Class<T> clazz, DateTimeFormatter... formattersOpt) {
        Class<?> clazzWrapper = ClassUtils.primitiveToWrapper(clazz);

        if (s == null) {
            return Optional.empty();
        }

        T parsedObject;

        if (Boolean.class.getTypeName().equals(clazzWrapper.getTypeName())) {
            parsedObject = cast(Boolean.parseBoolean(s), clazz);
        } else if (clazzWrapper.isEnum()) {
            parsedObject = cast(s, clazz);
        } else if (Number.class.isAssignableFrom(clazzWrapper)) {
            parsedObject = cast(Double.parseDouble(s), clazz);
        } else if (List.of(Character.class.getTypeName(), String.class.getTypeName()).contains(clazzWrapper.getTypeName())) {
            parsedObject = cast(s, clazz);
        } else if (LocalDate.class.getTypeName().equals(clazzWrapper.getTypeName())) {
            parsedObject = cast(LocalDate.parse(s, PGUtils.findFirstParameter(formattersOpt).orElse(DateTimeFormatter.ISO_LOCAL_DATE)), clazz);
        } else if (LocalTime.class.getTypeName().equals(clazzWrapper.getTypeName())) {
            parsedObject = cast(LocalTime.parse(s, PGUtils.findFirstParameter(formattersOpt).orElse(DateTimeFormatter.ISO_LOCAL_TIME)), clazz);
        } else if (LocalDateTime.class.getTypeName().equals(clazzWrapper.getTypeName())) {
            parsedObject = cast(LocalDateTime.parse(s, PGUtils.findFirstParameter(formattersOpt).orElse(DateTimeFormatter.ISO_LOCAL_DATE_TIME)), clazz);
        } else if (ZonedDateTime.class.getTypeName().equals(clazzWrapper.getTypeName())) {
            parsedObject = cast(ZonedDateTime.parse(s, PGUtils.findFirstParameter(formattersOpt).orElse(DateTimeFormatter.ISO_ZONED_DATE_TIME)), clazz);
        } else {
            throw new ServiceException("Type " + clazz.getTypeName() + " is not managed !");
        }

        return Optional.ofNullable(parsedObject);
    }

    public boolean isDateObject(Class<?> clazz) {
        return switch (clazz.getName()) {
            case "java.time.LocalDate", "java.sql.Date", "java.time.LocalTime", "java.sql.Time",
                 "java.time.LocalDateTime", "java.sql.Timestamp", "java.time.ZonedDateTime" -> true;
            default -> false;
        };
    }
}