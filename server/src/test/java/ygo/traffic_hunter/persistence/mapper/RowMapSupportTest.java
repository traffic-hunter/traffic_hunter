package ygo.traffic_hunter.persistence.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import ygo.traffic_hunter.AbstractTestConfiguration;

class RowMapSupportTest extends AbstractTestConfiguration {

    @Test
    void 역직렬화를_테스트한다() throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();

        RowMapSupport<?> support = new RowMapSupport<>(objectMapper);

        List<String> strings = List.of("banana", "apple", "orange");

        String json = objectMapper.writeValueAsString(strings);

        List<String> result = support.deserializeList(json, String.class);

        for (int i = 0; i < strings.size(); i++) {
            assertThat(strings.get(i)).isEqualTo(result.get(i));
        }
    }
}