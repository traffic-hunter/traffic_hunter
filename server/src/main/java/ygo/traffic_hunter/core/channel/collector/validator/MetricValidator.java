package ygo.traffic_hunter.core.channel.collector.validator;

import java.util.Objects;
import ygo.traffic_hunter.core.annotation.Validator;
import ygo.traffic_hunter.dto.systeminfo.metadata.AgentMetadata;
import ygo.traffic_hunter.dto.systeminfo.metadata.AgentStatus;
import ygo.traffic_hunter.dto.systeminfo.metadata.MetadataWrapper;

@Validator
public class MetricValidator {

    public boolean validate(final MetadataWrapper<?> metric) {
        final AgentMetadata metadata = metric.metadata();

       if (Objects.isNull(metadata)) {
           return false;
       }

       return isCheck(metadata);
    }

    private boolean isCheck(final AgentMetadata metadata) {
        return Objects.equals(metadata.status(), AgentStatus.RUNNING)
                && !metadata.agentId().isEmpty()
                && !metadata.agentName().isEmpty()
                && !metadata.agentVersion().isEmpty();
    }
}
