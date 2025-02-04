/**
 * The MIT License
 * <p>
 * Copyright (c) 2024 yungwang-o
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package ygo.traffic_hunter.presentation.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ygo.traffic_hunter.core.identification.SessionIdentification;
import ygo.traffic_hunter.core.service.MetricService;
import ygo.traffic_hunter.domain.interval.TimeInterval;

/**
 * @author yungwang-o, JuSeong
 * @version 1.1.0
 */
@RestController
@RequiredArgsConstructor
public class ServerSentEventController {

    private static final long TIMEOUT = 3600000L;

    private final MetricService metricService;

    @GetMapping(path = "/metrics/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public SseEmitter subscribe(final HttpServletRequest request) {

        return metricService.register(new SessionIdentification(request.getSession().getId()), new SseEmitter(TIMEOUT));
    }

    @PostMapping("/metrics/broadcast/{interval}")
    @ResponseStatus(HttpStatus.OK)
    public void broadcast(final HttpServletRequest request,
                          @PathVariable(name = "interval") final TimeInterval interval,
                          @RequestParam(defaultValue = "20") final Integer limit) {

        metricService.scheduleBroadcast(new SessionIdentification(request.getSession().getId()), interval, limit);
    }
}
