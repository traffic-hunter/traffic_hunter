package ygo.traffichunter.retry.backoff;

public abstract class BackOffPolicy {

    private final long intervalMillis;
    private final int multiplier;

    public BackOffPolicy(final long intervalMillis, final int multiplier) {
        this.intervalMillis = intervalMillis;
        this.multiplier = multiplier;
    }

    public long getIntervalMillis() {
        return intervalMillis;
    }

    public int getMultiplier() {
        return multiplier;
    }

    @Override
    public String toString() {
        return "BackOffPolicy{" +
                "intervalMillis=" + intervalMillis +
                ", multiplier=" + multiplier +
                '}';
    }
}
