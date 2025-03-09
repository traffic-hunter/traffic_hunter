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
package org.traffichunter.javaagent.plugin.hibernate.helper;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import org.hibernate.Transaction;
import org.traffichunter.javaagent.plugin.sdk.instumentation.SpanScope;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
public class HibernateInstrumentationHelper {

    private static final String HIBERNATE_TRACE_SCOPE = "hibernate-trace";

    public static class TransactionHelper {

        public static SpanScope start(final Transaction transaction,
                                      final Context parentContext,
                                      final SessionInfo sessionInfo) {

            Span span = GlobalOpenTelemetry.getTracer(HIBERNATE_TRACE_SCOPE)
                    .spanBuilder("hibernate-transaction")
                    .setParent(parentContext)
                    .setAttribute("transaction.commit", sessionInfo.getSessionId())
                    .setAttribute("transaction.status", transaction.getStatus().name())
                    .startSpan();

            return new SpanScope(span, span.makeCurrent());
        }
    }

    public static class SessionHelper {

        public static SpanScope start(final String name,
                                      final String entityName,
                                      final Context parentContext,
                                      final SessionInfo sessionInfo) {

            Span span = GlobalOpenTelemetry.getTracer(HIBERNATE_TRACE_SCOPE)
                    .spanBuilder("hibernate-session-span")
                    .setParent(parentContext)
                    .setAttribute("session.method", name)
                    .setAttribute("session.entity.name", entityName)
                    .setAttribute("session.id", sessionInfo.getSessionId())
                    .startSpan();

            return new SpanScope(span, span.makeCurrent());
        }
    }

    public static class QueryHelper {

        public static SpanScope start(final String queryStr,
                                      final Context parentContext,
                                      final SessionInfo sessionInfo) {

            Span span = GlobalOpenTelemetry.getTracer(HIBERNATE_TRACE_SCOPE)
                    .spanBuilder("hibernate-query-span")
                    .setParent(parentContext)
                    .setAttribute("query", queryStr)
                    .setAttribute("session.id", sessionInfo.getSessionId())
                    .startSpan();

            return new SpanScope(span, span.makeCurrent());
        }
    }

    public static void end(final SpanScope spanScope, final Throwable throwable) {

        Span span = spanScope.span();
        Scope scope = spanScope.scope();

        if (throwable != null) {
            span.recordException(throwable);
            span.setStatus(StatusCode.ERROR, throwable.getMessage());
        }

        span.end();
        scope.close();
    }

    public static String get() {
        return HIBERNATE_TRACE_SCOPE;
    }
}
