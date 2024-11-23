package ygo.traffic_hunter.core.dto.request.metadata;

import ygo.traffic_hunter.core.dto.request.Metric;

public record MetadataWrapper<D extends Metric>(AgentMetadata metadata, D data) {
}
