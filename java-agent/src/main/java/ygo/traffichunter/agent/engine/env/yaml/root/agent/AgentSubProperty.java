package ygo.traffichunter.agent.engine.env.yaml.root.agent;

import ygo.traffichunter.agent.engine.env.yaml.root.agent.retry.RetrySubProperty;

public class AgentSubProperty {

    private String serverUri;
    private String target;
    private int interval;
    private RetrySubProperty retry;

    public AgentSubProperty() {
    }

    public AgentSubProperty(final String serverUri, final String target, final int interval,
                            final RetrySubProperty retry) {
        this.serverUri = serverUri;
        this.target = target;
        this.interval = interval;
        this.retry = retry;
    }

    public String getServerUri() {
        return serverUri;
    }

    public void setServerUri(final String serverUri) {
        this.serverUri = serverUri;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(final String target) {
        this.target = target;
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


}
