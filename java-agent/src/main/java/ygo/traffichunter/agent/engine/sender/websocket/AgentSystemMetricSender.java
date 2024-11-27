package ygo.traffichunter.agent.engine.sender.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ygo.traffichunter.agent.engine.collect.MetricCollectSupport;
import ygo.traffichunter.agent.engine.sender.MetricSender;
import ygo.traffichunter.agent.engine.metric.systeminfo.SystemInfo;
import ygo.traffichunter.agent.engine.metric.metadata.AgentMetadata;
import ygo.traffichunter.agent.engine.metric.metadata.MetadataWrapper;
import ygo.traffichunter.websocket.MetricWebSocketClient;
import ygo.traffichunter.websocket.converter.SerializationByteArrayConverter.MetricType;

public class AgentSystemMetricSender implements MetricSender {

    private static final Logger log = LoggerFactory.getLogger(AgentSystemMetricSender.class);

    private final MetricWebSocketClient client;

    public AgentSystemMetricSender(final MetricWebSocketClient client) {
        this.client = client;
    }

    @Override
    public void toSend(final AgentMetadata metadata) {
        final SystemInfo systemInfo = MetricCollectSupport.collect();

        final MetadataWrapper<SystemInfo> wrapper = MetadataWrapper.create(metadata, systemInfo);

        client.compressToSend(wrapper, MetricType.SYSTEM_METRIC);
    }
}
