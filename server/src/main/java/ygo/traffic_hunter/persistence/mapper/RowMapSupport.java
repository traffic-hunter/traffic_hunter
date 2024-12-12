package ygo.traffic_hunter.persistence.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
@RequiredArgsConstructor
public class RowMapSupport<T> {

    private final ObjectMapper objectMapper;

    protected final String serialize0(final T object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    protected final String serialize0(final List<T> object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    protected <C> C deserialize(final String json, final Class<C> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    protected <C> List<C> deserializeList(final String json, Class<C> clazz) {
        try {

            if(json == null || json.isEmpty()) {
                return Collections.emptyList();
            }

            JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, clazz);

            return objectMapper.readValue(json, javaType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
