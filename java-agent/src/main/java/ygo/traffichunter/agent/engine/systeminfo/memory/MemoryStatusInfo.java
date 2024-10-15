package ygo.traffichunter.agent.engine.systeminfo.memory;

public record MemoryStatusInfo(MemoryUsage heapMemoryUsage, MemoryUsage nonHeapMemoryUsage) {

    public record MemoryUsage(long init, long used, long committed, long max) {}
}
