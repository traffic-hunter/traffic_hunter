package ygo.traffic_hunter.common.map;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.Named;
import ygo.traffic_hunter.core.dto.request.metadata.MetadataWrapper;
import ygo.traffic_hunter.core.dto.request.systeminfo.SystemInfo;
import ygo.traffic_hunter.core.dto.request.systeminfo.cpu.CpuStatusInfo;
import ygo.traffic_hunter.core.dto.request.systeminfo.gc.GarbageCollectionStatusInfo;
import ygo.traffic_hunter.core.dto.request.systeminfo.gc.collections.GarbageCollectionTime;
import ygo.traffic_hunter.core.dto.request.systeminfo.memory.MemoryStatusInfo;
import ygo.traffic_hunter.core.dto.request.systeminfo.runtime.RuntimeStatusInfo;
import ygo.traffic_hunter.core.dto.request.systeminfo.thread.ThreadStatusInfo;
import ygo.traffic_hunter.core.dto.response.SystemMetricResponse;
import ygo.traffic_hunter.domain.entity.MetricMeasurement;
import ygo.traffic_hunter.domain.metric.MetricData;
import ygo.traffic_hunter.domain.metric.cpu.CpuMetricMeasurement;
import ygo.traffic_hunter.domain.metric.gc.GCMetricMeasurement;
import ygo.traffic_hunter.domain.metric.gc.time.GCMetricCollectionTime;
import ygo.traffic_hunter.domain.metric.memory.MemoryMetricMeasurement;
import ygo.traffic_hunter.domain.metric.memory.usage.MemoryMetricUsage;
import ygo.traffic_hunter.domain.metric.runtime.RuntimeMetricMeasurement;
import ygo.traffic_hunter.domain.metric.thread.ThreadMetricMeasurement;

@Mapper(componentModel = ComponentModel.SPRING)
public interface SystemInfoMapper {

    @Mapping(target = "time", source = "data.time")
    @Mapping(target = "agentBootTime", source = "metadata.startTime")
    @Mapping(target = "agentVersion", source = "metadata.agentVersion")
    @Mapping(target = "agentName", source = "metadata.agentName")
    @Mapping(target = "agentId", source = "metadata.agentId")
    @Mapping(target = "metricData", source = "data", qualifiedByName = "toMetricData")
    MetricMeasurement map(MetadataWrapper<SystemInfo> wrapper);

    SystemMetricResponse map(MetricMeasurement measurement);

    @Named("toMetricData")
    default MetricData mapMetricData(SystemInfo systemInfo) {
        return new MetricData(
                cpuStatusInfoToCpuMetricMeasurement(systemInfo.cpuStatusInfo()),
                gcStatusInfoToGCMetricMeasurement(systemInfo.garbageCollectionStatusInfo()),
                memoryStatusInfoToMemoryMetricMeasurement(systemInfo.memoryStatusInfo()),
                runtimeStatusInfoToRuntimeMetricMeasurement(systemInfo.runtimeStatusInfo()),
                threadStatusInfoToThreadMetricMeasurement(systemInfo.threadStatusInfo())
        );
    }

    CpuMetricMeasurement cpuStatusInfoToCpuMetricMeasurement(CpuStatusInfo cpuStatusInfo);

    @Mapping(target = "gcMetricCollectionTimes", source = "garbageCollectionTimes")
    GCMetricMeasurement gcStatusInfoToGCMetricMeasurement(GarbageCollectionStatusInfo gcStatusInfo);

    GCMetricCollectionTime gcTimeToGCMetricCollectionTime(GarbageCollectionTime gcTime);

    MemoryMetricMeasurement memoryStatusInfoToMemoryMetricMeasurement(MemoryStatusInfo memoryStatusInfo);

    MemoryMetricUsage memoryUsageToMemoryMetricUsage(MemoryStatusInfo.MemoryUsage memoryUsage);

    RuntimeMetricMeasurement runtimeStatusInfoToRuntimeMetricMeasurement(RuntimeStatusInfo runtimeStatusInfo);

    ThreadMetricMeasurement threadStatusInfoToThreadMetricMeasurement(ThreadStatusInfo threadStatusInfo);
}
