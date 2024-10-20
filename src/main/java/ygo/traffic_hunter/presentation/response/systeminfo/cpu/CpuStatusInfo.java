package ygo.traffic_hunter.presentation.response.systeminfo.cpu;

public record CpuStatusInfo(double systemCpuLoad, double processCpuLoad, long availableProcessors) {
}
