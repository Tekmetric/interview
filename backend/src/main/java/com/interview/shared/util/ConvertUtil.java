package com.interview.util;

public final class ConvertUtil {
    private ConvertUtil() {
    }

    public static <T, D> D convertToDTO(T entity, Class<D> dtoClass) {
        try {
            return dtoClass.getConstructor(entity.getClass()).newInstance(entity);
        } catch (Exception e) {
            throw new RuntimeException("Error converting to DTO", e);
        }
    }
}
