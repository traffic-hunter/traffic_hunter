package ygo.traffichunter.agent.engine.metric.systeminfo.cpu;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public record CpuStatusInfo(double systemCpuLoad, double processCpuLoad, long availableProcessors) {
}
