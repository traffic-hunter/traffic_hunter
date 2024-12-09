package ygo.traffic_hunter.core.dto.request.systeminfo.cpu;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public record CpuStatusInfo(double systemCpuLoad, double processCpuLoad, long availableProcessors) {
}
