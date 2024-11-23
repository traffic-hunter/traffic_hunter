package ygo.traffic_hunter.core.channel.collector.handler.systeminfo;

import lombok.Builder;
import ygo.traffic_hunter.common.map.SystemInfoMapper;
import ygo.traffic_hunter.core.channel.collector.handler.MetricHandler;
import ygo.traffic_hunter.core.channel.collector.processor.MetricProcessor;
import ygo.traffic_hunter.core.channel.collector.validator.MetricValidator;
import ygo.traffic_hunter.core.dto.request.metadata.MetadataWrapper;
import ygo.traffic_hunter.core.dto.request.systeminfo.SystemInfo;
import ygo.traffic_hunter.core.repository.MetricRepository;

public class SysteminfoMetricHandler implements MetricHandler {

    private final MetricProcessor<SystemInfo> processor;

    private final SystemInfoMapper mapper;

    @Builder
    public SysteminfoMetricHandler(final MetricProcessor<SystemInfo> processor,
                                   final SystemInfoMapper mapper) {

        this.processor = processor;
        this.mapper = mapper;
    }

    @Override
    public void handle(final byte[] payload, final MetricValidator validator, final MetricRepository repository) {

        MetadataWrapper<SystemInfo> object = processor.process(payload);

        if(validator.validate(object)) {
            repository.save(mapper.map(object));
        }
    }
}
