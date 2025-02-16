package ygo.traffic_hunter.common.map.impl.threshold;

import org.springframework.stereotype.Component;
import ygo.traffic_hunter.common.map.ThresholdMapper;
import ygo.traffic_hunter.core.dto.request.metadata.MetadataWrapper;
import ygo.traffic_hunter.core.dto.request.systeminfo.SystemInfo;
import ygo.traffic_hunter.domain.entity.alarm.Threshold;

@Component
public class ThresholdMapperImpl implements ThresholdMapper {

    @Override
    public Threshold map(final MetadataWrapper<SystemInfo> wrapper) {

        final SystemInfo data = wrapper.data();

        return Threshold.builder()
                .cpuThreshold((int) data.cpuStatusInfo().processCpuLoad())
                .memoryThreshold((int) data.memoryStatusInfo().heapMemoryUsage().used())
                .threadThreshold(data.threadStatusInfo().threadCount())
                .webRequestThreshold((int) data.tomcatWebServerInfo().tomcatRequestInfo().requestCount())
                .webThreadThreshold(data.threadStatusInfo().threadCount())
                .dbcpThreshold(data.hikariDbcpInfo().activeConnections())
                .build();

    }

}
