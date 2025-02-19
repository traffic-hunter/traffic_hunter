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
package ygo.traffic_hunter.core.assembler.span;

import java.util.List;
import org.springframework.stereotype.Component;
import ygo.traffic_hunter.core.assembler.Assembler;
import ygo.traffic_hunter.domain.metric.TransactionData;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
@Component
public final class SpanAssembler implements Assembler<List<TransactionData>, SpanTreeNode> {

    private static final String SPAN_ROOT_PARENT_ID = "0000000000000000";

    @Override
    public SpanTreeNode assemble(final List<TransactionData> transactions) {

        if(transactions == null || transactions.isEmpty()) {
            return SpanTreeNode.NO_OP;
        }

        SpanTreeNode root = SpanTreeNode.NO_OP;

        for(TransactionData data : transactions) {
            if(data.parentSpanId().equals(SPAN_ROOT_PARENT_ID)) {
                root = new SpanTreeNode(data);
                break;
            }
        }

        build(root, transactions);

        return root;
    }

    private void build(final SpanTreeNode parent, final List<TransactionData> transactions) {

        for(TransactionData data : transactions) {

            if(data.parentSpanId().equals(SPAN_ROOT_PARENT_ID)) {
                continue;
            }

            if(data.parentSpanId().equals(parent.getData().spanId())) {
                SpanTreeNode child = new SpanTreeNode(data);

                parent.addChildren(child);
                build(child, transactions);
            }
        }
    }
}
