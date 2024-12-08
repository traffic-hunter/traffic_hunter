package ygo.traffic_hunter.core.collector.validator;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import ygo.traffic_hunter.core.annotation.Validator;
import ygo.traffic_hunter.core.dto.request.metadata.AgentMetadata;
import ygo.traffic_hunter.core.dto.request.metadata.AgentStatus;
import ygo.traffic_hunter.core.dto.request.metadata.MetadataWrapper;
import ygo.traffic_hunter.core.repository.AgentRepository;

@Validator
@RequiredArgsConstructor
public class MetricValidator {

    private final AgentRepository agentRepository;

    public boolean validate(final MetadataWrapper<?> metric) {
        final AgentMetadata metadata = metric.metadata();

       if (Objects.isNull(metadata)) {
           return true;
       }

       return !isCheck(metadata);
    }

    private boolean isCheck(final AgentMetadata metadata) {
        return Objects.equals(metadata.status(), AgentStatus.RUNNING)
                && !metadata.agentId().isEmpty()
                && !metadata.agentName().isEmpty()
                && !metadata.agentVersion().isEmpty()
                && agentRepository.existsByAgentId(metadata.agentId());
    }
}
