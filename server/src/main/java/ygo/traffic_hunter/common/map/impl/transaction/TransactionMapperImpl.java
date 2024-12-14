/**
 * The MIT License
 *
 * Copyright (c) 2024 yungwang-o
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
package ygo.traffic_hunter.common.map.impl.transaction;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ygo.traffic_hunter.common.map.TransactionMapper;
import ygo.traffic_hunter.core.dto.request.metadata.AgentMetadata;
import ygo.traffic_hunter.core.dto.request.metadata.MetadataWrapper;
import ygo.traffic_hunter.core.dto.response.TransactionMetricResponse;
import ygo.traffic_hunter.core.repository.AgentRepository;
import ygo.traffic_hunter.domain.entity.Agent;
import ygo.traffic_hunter.domain.entity.TransactionMeasurement;
import ygo.traffic_hunter.domain.metric.TraceInfo;
import ygo.traffic_hunter.domain.metric.TransactionData;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
@Component
@RequiredArgsConstructor
public class TransactionMapperImpl implements TransactionMapper {

    private final AgentRepository agentRepository;

    @Override
    public TransactionMeasurement map(final MetadataWrapper<TraceInfo> wrapper) {

        AgentMetadata metadata = wrapper.metadata();

        TraceInfo data = wrapper.data();

        Agent agent = agentRepository.findByAgentId(metadata.agentId());

        return new TransactionMeasurement(
                data.startTime(),
                agent.id(),
                getTransactionData(data)
        );
    }

    @Override
    public TransactionMetricResponse map(final TransactionMeasurement measurement) {

        Agent agent = agentRepository.findById(measurement.agentId());

        return new TransactionMetricResponse(
                agent.agentName(),
                agent.agentBootTime(),
                agent.agentVersion(),
                measurement.transactionData()
        );
    }

    private TransactionData getTransactionData(final TraceInfo data) {
        return TransactionData.from(data);
    }
}
