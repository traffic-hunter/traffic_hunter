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
package ygo.traffic_hunter.core.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import ygo.traffic_hunter.core.assembler.Assembler;
import ygo.traffic_hunter.core.assembler.span.SpanTreeNode;
import ygo.traffic_hunter.core.dto.response.statistics.metric.StatisticsMetricAvgResponse;
import ygo.traffic_hunter.core.dto.response.statistics.metric.StatisticsMetricMaxResponse;
import ygo.traffic_hunter.core.dto.response.statistics.transaction.ServiceTransactionResponse;
import ygo.traffic_hunter.core.repository.MetricRepository;
import ygo.traffic_hunter.core.statistics.StatisticsMetricTimeRange;
import ygo.traffic_hunter.domain.metric.TransactionData;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MetricStatisticsService {

    private final MetricRepository metricRepository;

    private final Assembler<List<TransactionData>, SpanTreeNode> assembler;

    public Slice<ServiceTransactionResponse> retrieveServiceTransactions(final Pageable pageable) {

        return metricRepository.findServiceTransaction(pageable);
    }

    public SpanTreeNode retrieveSpanTree(final String requestUri, final String traceId) {

        if(!requestUri.startsWith("/")) {
            throw new IllegalArgumentException("Invalid request uri: " + requestUri);
        }

        List<TransactionData> transactionDatas = metricRepository.findTxDataByRequestUri(requestUri, traceId);

        return assembler.assemble(transactionDatas);
    }

    public StatisticsMetricMaxResponse retrieveStatisticsMaxMetric(final StatisticsMetricTimeRange timeRange) {

        return metricRepository.findMaxMetricByTimeInterval(timeRange);
    }

    public StatisticsMetricAvgResponse retrieveStatisticsAvgMetric(final StatisticsMetricTimeRange timeRange) {

        return metricRepository.findAvgMetricByTimeInterval(timeRange);
    }
}
