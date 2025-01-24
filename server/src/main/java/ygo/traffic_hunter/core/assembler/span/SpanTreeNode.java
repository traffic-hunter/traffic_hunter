package ygo.traffic_hunter.core.assembler.span;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.ToString;
import ygo.traffic_hunter.domain.metric.TransactionData;

@Getter
@ToString
public class SpanTreeNode {

    public static final SpanTreeNode NO_OP = new SpanTreeNode(null);

    private final TransactionData data;

    private final List<SpanTreeNode> children = new ArrayList<>();

    public SpanTreeNode(final TransactionData data) {
        this.data = data;
    }

    public void addChildren(final SpanTreeNode node) {
        children.add(node);
    }

    public void removeChildren(final SpanTreeNode node) {
        children.remove(node);
    }

    public int size() {
        return children.size();
    }
}
