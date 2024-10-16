package ygo.traffichunter.agent.engine.systeminfo.runtime;

public record RuntimeStatusInfo(
        long getStartTime,
        long getUpTime,
        String getVmName,
        String getVmVersion
) {
}
