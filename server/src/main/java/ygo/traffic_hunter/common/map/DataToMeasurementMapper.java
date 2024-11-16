package ygo.traffic_hunter.common.map;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;
import ygo.traffic_hunter.domain.measurement.metric.MetricMeasurement;
import ygo.traffic_hunter.domain.measurement.metric.cpu.CpuMetricMeasurement;
import ygo.traffic_hunter.domain.measurement.metric.gc.GCMetricMeasurement;
import ygo.traffic_hunter.domain.measurement.metric.gc.time.GCMetricCollectionTime;
import ygo.traffic_hunter.domain.measurement.metric.memory.MemoryMetricMeasurement;
import ygo.traffic_hunter.domain.measurement.metric.memory.usage.MemoryMetricUsage;
import ygo.traffic_hunter.domain.measurement.metric.runtime.RuntimeMetricMeasurement;
import ygo.traffic_hunter.domain.measurement.metric.thread.ThreadMetricMeasurement;
import ygo.traffic_hunter.presentation.response.systeminfo.SystemInfo;
import ygo.traffic_hunter.presentation.response.systeminfo.cpu.CpuStatusInfo;
import ygo.traffic_hunter.presentation.response.systeminfo.gc.GarbageCollectionStatusInfo;
import ygo.traffic_hunter.presentation.response.systeminfo.gc.collections.GarbageCollectionTime;
import ygo.traffic_hunter.presentation.response.systeminfo.memory.MemoryStatusInfo;
import ygo.traffic_hunter.presentation.response.systeminfo.runtime.RuntimeStatusInfo;
import ygo.traffic_hunter.presentation.response.systeminfo.thread.ThreadStatusInfo;

@Mapper(componentModel = ComponentModel.SPRING)
public interface DataToMeasurementMapper {

    @Mapping(target = "clientIp", ignore = true) // Assuming this is set elsewhere
    @Mapping(source = "cpuStatusInfo", target = "cpuMetric")
    @Mapping(source = "garbageCollectionStatusInfo", target = "gcMetric")
    @Mapping(source = "memoryStatusInfo", target = "memoryMetric")
    @Mapping(source = "runtimeStatusInfo", target = "runtimeMetric")
    @Mapping(source = "threadStatusInfo", target = "threadMetric")
    MetricMeasurement systemInfoToMetricMeasurement(SystemInfo systemInfo);

    CpuMetricMeasurement cpuStatusInfoToCpuMetricMeasurement(CpuStatusInfo cpuStatusInfo);

    @Mapping(target = "gcMetricCollectionTimes", source = "garbageCollectionTimes")
    GCMetricMeasurement gcStatusInfoToGCMetricMeasurement(GarbageCollectionStatusInfo gcStatusInfo);

    GCMetricCollectionTime gcTimeToGCMetricCollectionTime(GarbageCollectionTime gcTime);

    MemoryMetricMeasurement memoryStatusInfoToMemoryMetricMeasurement(MemoryStatusInfo memoryStatusInfo);

    MemoryMetricUsage memoryUsageToMemoryMetricUsage(MemoryStatusInfo.MemoryUsage memoryUsage);

    RuntimeMetricMeasurement runtimeStatusInfoToRuntimeMetricMeasurement(RuntimeStatusInfo runtimeStatusInfo);

    ThreadMetricMeasurement threadStatusInfoToThreadMetricMeasurement(ThreadStatusInfo threadStatusInfo);
}
