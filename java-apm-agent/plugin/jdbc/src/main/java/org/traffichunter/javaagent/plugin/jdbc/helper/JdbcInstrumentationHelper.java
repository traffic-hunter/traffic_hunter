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
package org.traffichunter.javaagent.plugin.jdbc.helper;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import org.traffichunter.javaagent.trace.manager.TraceManager;
import org.traffichunter.javaagent.trace.manager.TraceManager.SpanScope;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
public class JdbcInstrumentationHelper {

    private static final String JDBC_INSTRUMENTATION_SCOPE_NAME = "jdbc-tracer";

    public static class ConnectionInstrumentation {

        public static SpanScope start(final String sql, final Context parentContext) {

            Span span = TraceManager.getTracer(JDBC_INSTRUMENTATION_SCOPE_NAME)
                    .spanBuilder("connectionSpan")
                    .setParent(parentContext)
                    .setAttribute("sql", sql)
                    .startSpan();

            return new SpanScope(span, span.makeCurrent());
        }
    }

    public static class StatementInstrumentation {

        public static SpanScope start(final String sql, final Context parentContext) {

            Span span = TraceManager.getTracer(JDBC_INSTRUMENTATION_SCOPE_NAME)
                    .spanBuilder("statementSpan")
                    .setParent(parentContext)
                    .setAttribute("sql", sql)
                    .startSpan();

            return new SpanScope(span, span.makeCurrent());
        }
    }

    public static class PreparedStatementInstrumentation {

        public static SpanScope start(final Context parentContext) {

            Span span = TraceManager.getTracer(JDBC_INSTRUMENTATION_SCOPE_NAME)
                    .spanBuilder("preparedStatementSpan")
                    .setParent(parentContext)
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
}
