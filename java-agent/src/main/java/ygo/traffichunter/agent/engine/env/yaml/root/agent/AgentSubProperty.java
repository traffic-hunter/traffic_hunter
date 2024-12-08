package ygo.traffichunter.agent.engine.env.yaml.root.agent;

import ygo.traffichunter.agent.engine.env.yaml.root.agent.retry.RetrySubProperty;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public class AgentSubProperty {

    private String name;
    private String jar;
    private String serverUri;
    private String targetUri;
    private int interval;
    private RetrySubProperty retry;

    public AgentSubProperty() {
    }

    public AgentSubProperty(final String name, final String jar, final String serverUri, final String targetUri,
                            final int interval,
                            final RetrySubProperty retry) {
        this.name = name;
        this.jar = jar;
        this.serverUri = serverUri;
        this.targetUri = targetUri;
        this.interval = interval;
        this.retry = retry;
    }

    public String getServerUri() {
        return serverUri;
    }

    public void setServerUri(final String serverUri) {
        this.serverUri = serverUri;
    }

    public String getTargetUri() {
        return targetUri;
    }

    public void setTargetUri(final String targetUri) {
        this.targetUri = targetUri;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(final int interval) {
        this.interval = interval;
    }

    public RetrySubProperty getRetry() {
        return retry;
    }

    public void setRetry(final RetrySubProperty retry) {
        this.retry = retry;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getJar() {
        return jar;
    }

    public void setJar(final String jar) {
        this.jar = jar;
    }
}
