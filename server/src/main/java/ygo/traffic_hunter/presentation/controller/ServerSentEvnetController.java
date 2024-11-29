package ygo.traffic_hunter.presentation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ygo.traffic_hunter.core.service.MetricService;
import ygo.traffic_hunter.domain.interval.TimeInterval;

@RestController
@RequiredArgsConstructor
public class ServerSentEvnetController {

    private final MetricService metricService;

    @GetMapping(path = "/metrics/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public SseEmitter subscribe() {
        return metricService.register(new SseEmitter());
    }

    @PostMapping("/metrics/broadcast/")
    public void broadcast(final TimeInterval interval) {
        metricService.broadcast(interval);
    }
}
