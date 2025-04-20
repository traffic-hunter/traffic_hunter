package ygo.traffic_hunter.core.assembler.span;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import ygo.traffic_hunter.AbstractTestConfiguration;
import ygo.traffic_hunter.core.assembler.Assembler;
import ygo.traffic_hunter.domain.metric.TransactionData;

class SpanAssemblerTest extends AbstractTestConfiguration {

    @Test
    void assembler가_데이터를_잘_출력하는지_확인한다() {

        List<TransactionData> datas = new ArrayList<>();

        datas.add(createTransactionData("0000000000000000", "a"));
        datas.add(createTransactionData("a", "b"));
        datas.add(createTransactionData("b", "c"));
        datas.add(createTransactionData("c", "d"));

        Assembler<List<TransactionData>, SpanTreeNode> assembler = new SpanAssembler();

        SpanTreeNode spanTreeNode = assembler.assemble(datas);

        System.out.println(spanTreeNode);
    }

    private TransactionData createTransactionData(final String parentSpanId, final String spanId) {
        return TransactionData.builder()
                .parentSpanId(parentSpanId)
                .spanId(spanId)
                .traceId("traceId")
                .ended(true)
                .name("test")
                .attributesCount(4)
                .attributes(Map.of())
                .endTime(Instant.now())
                .startTime(Instant.now())
                .exception("exception")
                .duration(30)
                .build();
    }
}