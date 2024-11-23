package ygo.traffic_hunter.core.dto.request.systeminfo.cpu;

public record CpuStatusInfo(double systemCpuLoad, double processCpuLoad, long availableProcessors) {
}
