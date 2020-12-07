/*
 * Copyright (c) 2020 ProDev+ (Pascal Gerner).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.simplelib.helper;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ClassHelper {
    private ClassHelper() {
    }

    // Create Instance
    public static Object createInstance(Class<?> targetClass) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        return createInstance(targetClass, null);
    }

    public static Object createInstance(Class<?> targetClass, Object[] args) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        if (targetClass == null) throw new NullPointerException("Missing target class");
        if (args == null) args = new Object[0];

        Class<?>[] parameterTypes = getParameterTypes(args);
        if (parameterTypes == null) throw new IllegalArgumentException("Parameter types not found");

        return createInstance(targetClass, parameterTypes, args);
    }

    public static Object createInstance(Class<?> targetClass, Class<?>[] initParameterTypes, Object[] args) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        if (targetClass == null) throw new NullPointerException("Missing target class");
        if (initParameterTypes == null) initParameterTypes = new Class<?>[0];
        if (args == null) args = new Object[0];

        if (args.length < initParameterTypes.length) throw new IllegalArgumentException("More parameters than arguments");

        Constructor<?> constructor = findConstructor(targetClass, initParameterTypes);
        if (constructor == null) throw new IllegalArgumentException("Constructor not found");

        return createInstance(constructor, args);
    }

    public static <T> T createInstance(Constructor<T> constructor, Object[] args) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        if (constructor == null) throw new NullPointerException("Missing constructor");
        if (args == null) args = new Object[0];

        constructor.setAccessible(true);

        return constructor.newInstance(args);
    }

    // Invoke Method
    public static Object invoke(Object targetObj, String methodName) throws InvocationTargetException, IllegalAccessException {
        return invoke(targetObj, methodName, null);
    }

    public static Object invoke(Object targetObj, String methodName, Object[] args) throws InvocationTargetException, IllegalAccessException {
        if (targetObj == null) throw new NullPointerException("Missing target object");
        if (methodName == null) throw new NullPointerException("Missing method name");
        if (args == null) args = new Object[0];

        Class<?>[] parameterTypes = getParameterTypes(args);
        if (parameterTypes == null) throw new IllegalArgumentException("Parameter types not found");

        return invoke(targetObj, methodName, parameterTypes, args);
    }

    public static Object invoke(Object targetObj, String methodName, Class<?>[] methodParameterTypes, Object[] args) throws InvocationTargetException, IllegalAccessException {
        if (targetObj == null) throw new NullPointerException("Missing target object");
        if (methodName == null) throw new NullPointerException("Missing method name");
        if (methodParameterTypes == null) methodParameterTypes = new Class<?>[0];
        if (args == null) args = new Object[0];

        if (args.length < methodParameterTypes.length) throw new IllegalArgumentException("More parameters than arguments");

        Class<?> targetClass = targetObj.getClass();
        if (targetClass == null) throw new IllegalArgumentException("Target class not found");

        Method method = findMethod(targetClass, methodName, methodParameterTypes);
        if (method == null) throw new IllegalArgumentException("Method not found");

        return invoke(targetObj, method, args);
    }

    public static Object invoke(Object targetObj, Method method, Object[] args) throws InvocationTargetException, IllegalAccessException {
        if (targetObj == null) throw new NullPointerException("Missing target object");
        if (method == null) throw new NullPointerException("Missing method");
        if (args == null) args = new Object[0];

        method.setAccessible(true);

        return method.invoke(targetObj, args);
    }

    // Read field
    public static Object get(Object targetObj, String fieldName) throws IllegalAccessException {
        if (targetObj == null) throw new NullPointerException("Missing target object");
        if (fieldName == null) throw new NullPointerException("Missing field name");

        Class<?> targetClass = targetObj.getClass();
        if (targetClass == null) throw new IllegalArgumentException("Target class not found");

        Field field = findField(targetClass, null, fieldName);
        if (field == null) throw new IllegalArgumentException("Field not found");

        return get(targetObj, field);
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(Object targetObj, Class<T> fieldType, String fieldName) throws IllegalAccessException {
        if (targetObj == null) throw new NullPointerException("Missing target object");
        if (fieldName == null) throw new NullPointerException("Missing field name");

        Class<?> targetClass = targetObj.getClass();
        if (targetClass == null) throw new IllegalArgumentException("Target class not found");

        Field field = findField(targetClass, fieldType, fieldName);
        if (field == null) throw new IllegalArgumentException("Field not found");

        return (T) get(targetObj, field);
    }

    public static Object get(Object targetObj, Field field) throws IllegalAccessException {
        if (targetObj == null) throw new NullPointerException("Missing target object");
        if (field == null) throw new NullPointerException("Missing field");

        field.setAccessible(true);

        return field.get(targetObj);
    }

    // Modify field
    public static void set(Object targetObj, String fieldName, Object value) throws IllegalAccessException {
        if (targetObj == null) throw new NullPointerException("Missing target object");
        if (fieldName == null) throw new NullPointerException("Missing field name");

        Class<?> targetClass = targetObj.getClass();
        if (targetClass == null) throw new IllegalArgumentException("Target class not found");

        Field field = findField(targetClass, null, fieldName);
        if (field == null) throw new IllegalArgumentException("Field not found");

        set(targetObj, field, value);
    }

    @SuppressWarnings("unchecked")
    public static <T> void set(Object targetObj, Class<T> fieldType, String fieldName, T value) throws IllegalAccessException {
        if (targetObj == null) throw new NullPointerException("Missing target object");
        if (fieldName == null) throw new NullPointerException("Missing field name");

        Class<?> targetClass = targetObj.getClass();
        if (targetClass == null) throw new IllegalArgumentException("Target class not found");

        Field field = findField(targetClass, fieldType, fieldName);
        if (field == null) throw new IllegalArgumentException("Field not found");

        set(targetObj, field, value);
    }

    public static void set(Object targetObj, Field field, Object value) throws IllegalAccessException {
        if (targetObj == null) throw new NullPointerException("Missing target object");
        if (field == null) throw new NullPointerException("Missing field");

        field.setAccessible(true);

        field.set(targetObj, value);
    }

    // Find
    public static Constructor<?> findConstructor(Class<?> searchClass, Class<?>[] types) {
        if (searchClass == null) throw new NullPointerException("Missing class to be searched");
        if (types == null) throw new NullPointerException("Missing parameter types");

        while (searchClass != null) {
            for (Constructor<?> constructor : searchClass.getDeclaredConstructors()) {
                try {
                    constructor.setAccessible(true);

                    Class<?>[] constructorTypes = constructor.getParameterTypes();
                    if (constructorTypes == null) continue;

                    if (parameterTypesAssignableFrom(constructorTypes, types))
                        return constructor;
                } catch (Exception e) {
                }
            }
            searchClass = searchClass.getSuperclass();
        }
        return null;
    }

    public static Method findMethod(Class<?> searchClass, String name, Class<?>[] types) {
        if (searchClass == null) throw new NullPointerException("Missing class to be searched");
        if (name == null) throw new NullPointerException("Missing method name");
        if (types == null) throw new NullPointerException("Missing parameter types");

        while (searchClass != null) {
            for (Method method : searchClass.getDeclaredMethods()) {
                try {
                    String methodName = method.getName();
                    if (methodName == null || !methodName.equals(name)) continue;

                    method.setAccessible(true);

                    Class<?>[] methodTypes = method.getParameterTypes();
                    if (methodTypes == null) continue;

                    if (parameterTypesAssignableFrom(methodTypes, types))
                        return method;
                } catch (Exception e) {
                }
            }
            searchClass = searchClass.getSuperclass();
        }
        return null;
    }

    public static Field findField(Class<?> searchClass, Class<?> type, String name) {
        if (searchClass == null) throw new NullPointerException("Missing class to be searched");
        if (name == null) throw new NullPointerException("Missing field name");

        while (searchClass != null) {
            for (Field field : searchClass.getDeclaredFields()) {
                try {
                    if (type != null) {
                        Class<?> fieldType = field.getType();
                        if (fieldType == null || !fieldType.isAssignableFrom(type)) continue;
                    }

                    String fieldName = field.getName();
                    if (fieldName == null || !fieldName.equals(name)) continue;

                    field.setAccessible(true);

                    return field;
                } catch (Exception e) {
                }
            }
            searchClass = searchClass.getSuperclass();
        }
        return null;
    }

    // Parameter Types
    public static Class<?>[] getParameterTypes(Object[] args) {
        if (args == null) args = new Object[0];

        int typeCount = args.length;

        Class<?>[] types = new Class<?>[typeCount];
        for (int pos = 0; pos < typeCount; pos++) {
            Object obj = args[pos];

            Class<?> type = null;
            if (obj != null)
                type = obj.getClass();
            types[pos] = type;
        }
        return types;
    }

    public static boolean parameterTypesAssignableFrom(Class<?>[] assignableTypes, Class<?>[] types) {
        if (assignableTypes == null || types == null) return false;
        if (assignableTypes.length != types.length) return false;

        int typeCount = Math.min(assignableTypes.length, types.length);
        for (int pos = 0; pos < typeCount; pos++) {
            Class<?> assignableType = assignableTypes[pos];
            Class<?> type = types[pos];

            if (assignableType == null) return false;
            if (type == null) continue;

            if (assignableType.isPrimitive()) {
                try {
                    assignableType = toWrapper(assignableType);
                } catch (Exception e) {
                }
            }
            if (type.isPrimitive()) {
                try {
                    type = toWrapper(type);
                } catch (Exception e) {
                }
            }

            if (!assignableType.isAssignableFrom(type))
                return false;
        }

        return true;
    }

    public static Class<?> toWrapper(Class<?> primitiveClass) {
        if (primitiveClass == null) throw new NullPointerException("Missing primitive class");
        if (!primitiveClass.isPrimitive()) throw new IllegalArgumentException("Not a primitive class");

        Object primitiveArray = Array.newInstance(primitiveClass, 1);
        return Array.get(primitiveArray, 0).getClass();
    }
}