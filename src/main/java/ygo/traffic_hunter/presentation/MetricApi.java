package ygo.traffic_hunter.presentation;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ygo.traffic_hunter.presentation.response.systeminfo.SystemInfo;
import ygo.traffic_hunter.common.util.ip.ClientInfoUtil;

@Slf4j
@RestController
public class MetricApi {

    @PostMapping("/traffic-hunter")
    public ResponseEntity<Void> metricApi(@RequestBody final SystemInfo systemInfo, final HttpServletRequest request) {

        log.info("received system info: {}", systemInfo);
        log.info("client ip: {}", ClientInfoUtil.getClientIp(request));
        log.info("target service = {}", systemInfo.targetJVM());

        return ResponseEntity.ok().build();
    }
}
