package ygo.traffichunter.agent.engine.metric.systeminfo.cpu;

public record CpuStatusInfo(double systemCpuLoad, double processCpuLoad, long availableProcessors) {
}
