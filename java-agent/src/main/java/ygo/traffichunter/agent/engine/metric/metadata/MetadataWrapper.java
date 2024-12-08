package ygo.traffichunter.agent.engine.metric.metadata;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public record MetadataWrapper<D>(AgentMetadata metadata, D data) {

    public static <D> MetadataWrapper<D> create(AgentMetadata metadata, D data) {
        return new MetadataWrapper<>(metadata, data);
    }
}
