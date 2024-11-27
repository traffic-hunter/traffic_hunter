package ygo.traffichunter.agent.engine.metric.systeminfo.memory;

public record MemoryStatusInfo(MemoryUsage heapMemoryUsage, MemoryUsage nonHeapMemoryUsage) {

    public record MemoryUsage(long init, long used, long committed, long max) {}
}
