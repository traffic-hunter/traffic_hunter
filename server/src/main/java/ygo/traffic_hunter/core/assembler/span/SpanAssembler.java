package ygo.traffic_hunter.core.assembler.span;

import java.util.List;
import org.springframework.stereotype.Component;
import ygo.traffic_hunter.core.assembler.Assembler;
import ygo.traffic_hunter.domain.metric.TransactionData;

@Component
public final class SpanAssembler implements Assembler<List<TransactionData>, SpanTreeNode> {

    private static final String SPAN_ROOT_PARENT_ID = "0000000000000000";

    @Override
    public SpanTreeNode assemble(final List<TransactionData> transactions) {

        if(transactions == null || transactions.isEmpty()) {
            throw new IllegalArgumentException("transactions is null or empty");
        }

        SpanTreeNode root = null;

        for(TransactionData data : transactions) {
            if(data.parentSpanId().equals(SPAN_ROOT_PARENT_ID)) {
                root = new SpanTreeNode(data);
                break;
            }
        }

        build(root, transactions);

        return root;
    }

    private void build(SpanTreeNode parent, List<TransactionData> transactions) {

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
