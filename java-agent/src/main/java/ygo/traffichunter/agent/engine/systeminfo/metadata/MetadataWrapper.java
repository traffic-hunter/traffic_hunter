package ygo.traffichunter.agent.engine.systeminfo.metadata;

public class MetadataWrapper<D> {

    private final AgentMetadata metadata;

    private final D data;

    public MetadataWrapper(final AgentMetadata metadata, final D data) {
        this.metadata = metadata;
        this.data = data;
    }
}
