package ygo.traffichunter.websocket.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import ygo.AbstractTest;
import ygo.traffichunter.agent.AgentStatus;
import ygo.traffichunter.agent.engine.collect.MetricCollectSupport;
import ygo.traffichunter.agent.engine.metric.systeminfo.SystemInfo;
import ygo.traffichunter.agent.engine.metric.metadata.AgentMetadata;
import ygo.traffichunter.agent.engine.metric.metadata.MetadataWrapper;
import ygo.traffichunter.websocket.converter.SerializationByteArrayConverter.MetricType;

class SerializationByteArrayConverterTest extends AbstractTest {

    private final SerializationByteArrayConverter converter;
    private final ObjectMapper mapper;

    public SerializationByteArrayConverterTest() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        this.mapper = mapper;
        this.converter = new SerializationByteArrayConverter(this.mapper);
    }

    @Test
    void byte_직렬화의_데이터_사이즈를_확인한다() throws JsonProcessingException {
        // given
        AgentMetadata metadata = new AgentMetadata(
                UUID.randomUUID().toString(),
                "1.0.0",
                "myAgent",
                Instant.now(),
                new AtomicReference<>(AgentStatus.RUNNING)
        );

        SystemInfo collect = MetricCollectSupport.collect();
        MetadataWrapper<SystemInfo> metadataWrapper = MetadataWrapper.create(metadata, collect);

        // when
        String data = mapper.writeValueAsString(metadataWrapper);
        byte[] transform = converter.transform(metadataWrapper, MetricType.SYSTEM_METRIC);

        // then
        System.out.println(transform.length + " " + data.length());
    }

    @Test
    void byte_직렬화를_한_데이터를_역직렬화_한다() {
        AgentMetadata metadata = new AgentMetadata(
                UUID.randomUUID().toString(),
                "1.0.0",
                "myAgent",
                Instant.now(),
                new AtomicReference<>(AgentStatus.RUNNING)
        );

        SystemInfo collect = MetricCollectSupport.collect();
        MetadataWrapper<SystemInfo> metadataWrapper = MetadataWrapper.create(metadata, collect);
        byte[] transform = converter.transform(metadataWrapper, MetricType.SYSTEM_METRIC);

        // when
        MetadataWrapper<SystemInfo> systemInfoMetadataWrapper = converter.inverseTransform(transform, new TypeReference<>() {});

        // then
        System.out.println(systemInfoMetadataWrapper);
    }
}