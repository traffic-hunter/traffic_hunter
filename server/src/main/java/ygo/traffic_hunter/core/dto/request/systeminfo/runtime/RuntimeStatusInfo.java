package ygo.traffic_hunter.core.dto.request.systeminfo.runtime;

public record RuntimeStatusInfo(
        long getStartTime,
        long getUpTime,
        String getVmName,
        String getVmVersion
) {
}
