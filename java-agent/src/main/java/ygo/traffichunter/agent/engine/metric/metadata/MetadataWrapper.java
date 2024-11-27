package ygo.traffichunter.agent.engine.metric.metadata;

public record MetadataWrapper<D>(AgentMetadata metadata, D data) {

    public static <D> MetadataWrapper<D> create(AgentMetadata metadata, D data) {
        return new MetadataWrapper<>(metadata, data);
    }
}
