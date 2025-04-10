package org.traffichunter.javaagent.websocket.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.traffichunter.javaagent.commons.status.AgentStatus;
import org.traffichunter.javaagent.websocket.converter.SerializationByteArrayConverter.MetricType;
import org.traffichunter.javaagent.websocket.metadata.Metadata;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
@DisplayNameGeneration(ReplaceUnderscores.class)
class SerializationByteArrayConverterTest {

    private final SerializationByteArrayConverter converter;

    public SerializationByteArrayConverterTest() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .registerModule(new JavaTimeModule());

        this.converter = new SerializationByteArrayConverter(mapper);
    }

    @Test
    void text_to_compress_binary_size_test() {
        // given
        Metadata metadata = Metadata.builder()
                .status(AgentStatus.RUNNING)
                .startTime(Instant.now())
                .agentId(UUID.randomUUID().toString())
                .agentVersion("1.0.0")
                .agentName("payments-service-agent")
                .build();

        // when
        byte[] byteArr = converter.transform(metadata, MetricType.SYSTEM_METRIC);

        // then
        System.out.println(byteArr.length);
    }
}