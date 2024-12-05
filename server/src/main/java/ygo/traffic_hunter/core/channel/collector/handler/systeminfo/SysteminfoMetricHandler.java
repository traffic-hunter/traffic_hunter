package ygo.traffic_hunter.core.channel.collector.handler.systeminfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ygo.traffic_hunter.common.map.SystemInfoMapper;
import ygo.traffic_hunter.core.channel.collector.handler.MetricHandler;
import ygo.traffic_hunter.core.channel.collector.processor.MetricProcessor;
import ygo.traffic_hunter.core.channel.collector.validator.MetricValidator;
import ygo.traffic_hunter.core.dto.request.metadata.MetadataWrapper;
import ygo.traffic_hunter.core.dto.request.systeminfo.SystemInfo;
import ygo.traffic_hunter.core.repository.MetricRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class SysteminfoMetricHandler implements MetricHandler {

    private final MetricProcessor processor;

    private final SystemInfoMapper mapper;

    private final MetricValidator validator;

    private final MetricRepository repository;

    @Override
    public byte getHeader() {
        return 1;
    }

    @Override
    @Transactional
    public void handle(final byte[] payload) {

        MetadataWrapper<SystemInfo> object = processor.processSystemInfo(payload);

        log.info("process system info: {}", object);

        if(validator.validate(object)) {
            repository.save(mapper.map(object));
        }
    }
}
