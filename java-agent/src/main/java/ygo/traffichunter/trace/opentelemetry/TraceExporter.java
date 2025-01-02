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
package ygo.traffichunter.trace.opentelemetry;

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
