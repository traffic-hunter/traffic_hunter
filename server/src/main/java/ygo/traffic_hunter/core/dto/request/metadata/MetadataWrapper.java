package ygo.traffic_hunter.core.dto.request.metadata;

public record MetadataWrapper<D>(AgentMetadata metadata, D data) {
}
