package ru.metal.cashflow.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JSONUtils {

    /**
     * Convert object to JSON
     *
     * @param object objct
     * @return JSON
     */
    public static String toJSON(Object object) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Convert JSON to java object
     *
     * @param json        json
     * @param objectClass java object class
     * @param <T>         type of java object
     * @return java object
     * @throws IOException execption while converting
     */
    public static <T> T fromJSON(String json, Class<T> objectClass) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, objectClass);
    }
}
