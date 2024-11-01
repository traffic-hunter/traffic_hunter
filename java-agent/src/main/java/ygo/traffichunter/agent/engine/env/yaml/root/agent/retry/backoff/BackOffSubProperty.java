package ygo.traffichunter.agent.engine.env.yaml.root.agent.retry.backoff;

public class BackOffSubProperty {

    private long intervalMillis;
    private int multiplier;

    public BackOffSubProperty() {
    }

    public BackOffSubProperty(final long intervalMillis, final int multiplier) {
        this.intervalMillis = intervalMillis;
        this.multiplier = multiplier;
    }

    public long getIntervalMillis() {
        return intervalMillis;
    }

    public void setIntervalMillis(final long intervalMillis) {
        this.intervalMillis = intervalMillis;
    }

    public int getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(final int multiplier) {
        this.multiplier = multiplier;
    }
}
