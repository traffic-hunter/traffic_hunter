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
package ygo.traffic_hunter.core.dto.response;

import java.time.Instant;
import ygo.traffic_hunter.core.assembler.span.SpanTreeNode;

/**
 * @author yungwang-o, JuSeong
 * @version 1.1.0
 */
public record TransactionMetricResponse(
        String agentName,
        Instant agentBootTime,
        String agentVersion,
        SpanTreeNode spanTreeNode
) {
    public static TransactionMetricResponse create(
            final String agentName,
            final Instant agentBootTime,
            final String agentVersion,
            final SpanTreeNode spanTreeNode) {

        return new TransactionMetricResponse(agentName, agentBootTime, agentVersion, spanTreeNode);
    }
}
