package ygo.traffichunter.agent.trace.opentelemetry;

import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.trace.data.SpanData;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ygo.traffichunter.agent.engine.metric.transaction.TraceInfo;
import ygo.traffichunter.agent.engine.queue.SyncQueue;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public class TraceExporter implements SpanExporter {

    private static final Logger log = LoggerFactory.getLogger(TraceExporter.class);

    private volatile boolean isShutdown = false;

    @Override
    public CompletableResultCode export(final Collection<SpanData> spans) {

        if(isShutdown) {
            return CompletableResultCode.ofFailure();
        }

        try {
            spans.stream()
                    .map(TraceInfo::translate)
                    .forEach(SyncQueue.INSTANCE::add);

            return CompletableResultCode.ofSuccess();
        } catch (RuntimeException e) {
            return CompletableResultCode.ofFailure();
        }
    }

    @Override
    public CompletableResultCode flush() {
        return CompletableResultCode.ofSuccess();
    }

    @Override
    public CompletableResultCode shutdown() {

        isShutdown = true;

        try {
            return CompletableResultCode.ofSuccess();
        } catch (Exception e) {
            return CompletableResultCode.ofFailure();
        }
    }
}
