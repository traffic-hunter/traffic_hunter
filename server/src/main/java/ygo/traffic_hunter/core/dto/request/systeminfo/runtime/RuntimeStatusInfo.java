package ygo.traffic_hunter.core.dto.request.systeminfo.runtime;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public record RuntimeStatusInfo(
        long getStartTime,
        long getUpTime,
        String getVmName,
        String getVmVersion
) {
}
