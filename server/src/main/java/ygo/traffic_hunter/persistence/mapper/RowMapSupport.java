package ygo.traffic_hunter.persistence.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
@RequiredArgsConstructor
public class RowMapSupport<T> {

    private final ObjectMapper objectMapper;

    protected String serialize(T object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    protected <C> C deserialize(String json, Class<C> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
