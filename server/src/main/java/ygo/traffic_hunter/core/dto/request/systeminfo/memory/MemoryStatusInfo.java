package ygo.traffic_hunter.core.dto.request.systeminfo.memory;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public record MemoryStatusInfo(MemoryUsage heapMemoryUsage, MemoryUsage nonHeapMemoryUsage) {

    public record MemoryUsage(long init, long used, long committed, long max) {}
}

