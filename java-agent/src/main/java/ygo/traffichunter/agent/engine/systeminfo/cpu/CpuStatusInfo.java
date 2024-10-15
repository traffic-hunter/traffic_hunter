package ygo.traffichunter.agent.engine.systeminfo.cpu;

public record CpuStatusInfo(double systemCpuLoad, double processCpuLoad, long availableProcessors) {
}
