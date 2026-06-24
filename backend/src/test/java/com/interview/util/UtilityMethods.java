package com.interview.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MvcResult;

public class UtilityMethods {
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static <T> T fromMvcResult(MvcResult result, Class<T> clazz) throws Exception {
        String json = result.getResponse().getContentAsString();
        return objectMapper.readValue(json, clazz);
    }
}
