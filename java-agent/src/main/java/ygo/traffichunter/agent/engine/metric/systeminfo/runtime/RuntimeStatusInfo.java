package ygo.traffichunter.agent.engine.metric.systeminfo.runtime;

public record RuntimeStatusInfo(
        long getStartTime,
        long getUpTime,
        String getVmName,
        String getVmVersion
) {
}
