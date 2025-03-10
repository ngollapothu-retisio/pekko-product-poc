package pekko.product.application.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ObjectMapperUtil {
    private static ObjectMapper objectMapper = new ObjectMapper();
    static {
        objectMapper.registerModule(new JavaTimeModule());
    }
    public static String toJsonString(Object object){
        if(object == null){
            return null;
        }
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("{}", e.getMessage());
        }
        return null;
    }
    public static <T> T getObject(String json, TypeReference<T> valueType) {
        if (json == null)
            return null;
        try {
            return objectMapper.readValue(json, valueType);
        } catch (Exception e) {
            log.error("Exception Occurred While Parsing the Object {}, {}", valueType, e);
        }
        return null;
    }
}
