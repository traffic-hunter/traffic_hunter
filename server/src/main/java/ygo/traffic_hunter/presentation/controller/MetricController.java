/**
 * The MIT License
 *
 * Copyright (c) 2024 traffic-hunter.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package ygo.traffic_hunter.presentation.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ygo.traffic_hunter.core.assembler.span.SpanTreeNode;
import ygo.traffic_hunter.core.dto.response.statistics.metric.StatisticsMetricAvgResponse;
import ygo.traffic_hunter.core.dto.response.statistics.metric.StatisticsMetricMaxResponse;
import ygo.traffic_hunter.core.dto.response.statistics.transaction.ServiceTransactionResponse;
import ygo.traffic_hunter.core.service.MetricStatisticsService;
import ygo.traffic_hunter.core.statistics.StatisticsMetricTimeRange;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/statistics")
public class MetricController {

    private final MetricStatisticsService metricStatisticsService;

    @GetMapping("/transaction")
    @ResponseStatus(HttpStatus.OK)
    public Slice<ServiceTransactionResponse> retrieveServiceTransactionApi(@PageableDefault(
                                                                                   direction = Direction.DESC
                                                                           ) final Pageable pageable) {

        return metricStatisticsService.retrieveServiceTransactions(pageable);
    }

    @GetMapping("/transaction/{traceId}")
    @ResponseStatus(HttpStatus.OK)
    public SpanTreeNode retrieveTransactionApi(@PathVariable @NotBlank String traceId) {

        return metricStatisticsService.retrieveSpanTree(traceId);
    }

    @GetMapping("/metric/max/{timeRange}")
    @ResponseStatus(HttpStatus.OK)
    public StatisticsMetricMaxResponse retrieveMaxMetricApi(@PathVariable @NotNull final StatisticsMetricTimeRange timeRange) {

        return metricStatisticsService.retrieveStatisticsMaxMetric(timeRange);
    }

    @GetMapping("/metric/avg/{timeRange}")
    @ResponseStatus(HttpStatus.OK)
    public StatisticsMetricAvgResponse retrieveAvgMetricApi(@PathVariable @NotNull final StatisticsMetricTimeRange timeRange) {

        return metricStatisticsService.retrieveStatisticsAvgMetric(timeRange);
    }
}
