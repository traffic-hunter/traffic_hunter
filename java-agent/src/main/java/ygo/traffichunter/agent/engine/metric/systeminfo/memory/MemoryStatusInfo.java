package ygo.traffichunter.agent.engine.metric.systeminfo.memory;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public record MemoryStatusInfo(MemoryUsage heapMemoryUsage, MemoryUsage nonHeapMemoryUsage) {

    public record MemoryUsage(long init, long used, long committed, long max) {}
}
