package ygo.traffic_hunter.presentation.response.systeminfo.runtime;

public record RuntimeStatusInfo(
        long getStartTime,
        long getUpTime,
        String getVmName,
        String getVmVersion
) {
}
