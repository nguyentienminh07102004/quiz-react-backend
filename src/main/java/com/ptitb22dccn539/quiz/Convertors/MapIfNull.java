package com.ptitb22dccn539.quiz.Convertors;

import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@SuppressWarnings(value = "unchecked")
@Component
public class MapIfNull<T> {

    public T mapIfNull(T source, T response) throws IllegalAccessException {
        Class<T> tClass = (Class<T>) source.getClass();
        Field[] fields = tClass.getDeclaredFields();
        for(Field field : fields) {
            field.setAccessible(true);
            if(field.get(response) == null) {
                field.set(response, field.get(source));
            }
        }
        return response;
    }
}
