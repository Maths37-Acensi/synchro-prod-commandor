package com.petg.blois.util;

import lombok.Data;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author 97211P
 */
@Data
public class ReflectionPropertyDescriptor {
    private Field field;
    private Method readMethod;
    private Method writeMethod;
}
