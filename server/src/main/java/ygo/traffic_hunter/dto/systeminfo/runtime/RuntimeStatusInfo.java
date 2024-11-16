package ygo.traffic_hunter.dto.systeminfo.runtime;

public record RuntimeStatusInfo(
        long getStartTime,
        long getUpTime,
        String getVmName,
        String getVmVersion
) {
}
