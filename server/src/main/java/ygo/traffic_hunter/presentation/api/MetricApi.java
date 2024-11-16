package ygo.traffic_hunter.presentation.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ygo.traffic_hunter.presentation.response.systeminfo.SystemInfo;
import ygo.traffic_hunter.presentation.response.systeminfo.metadata.MetadataWrapper;

@Slf4j
@RestController
public class MetricApi {

    @PostMapping("/traffic-hunter")
    public ResponseEntity<Void> metricApi(@RequestBody final MetadataWrapper<SystemInfo> systemInfo) {

        log.info("received system info: {}", systemInfo);

        return ResponseEntity.ok().build();
    }
}
