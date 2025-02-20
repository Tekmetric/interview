package com.interview.util;

public class ConvertUtil {
    public static <T, D> D convertToDTO(T entity, Class<D> dtoClass) {
        try {
            return dtoClass.getConstructor(entity.getClass()).newInstance(entity);
        } catch (Exception e) {
            throw new RuntimeException("Error converting to DTO", e);
        }
    }
}
