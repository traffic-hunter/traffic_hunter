package ygo.traffic_hunter.core.dto.request.metadata;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public record MetadataWrapper<D>(AgentMetadata metadata, D data) {
}
