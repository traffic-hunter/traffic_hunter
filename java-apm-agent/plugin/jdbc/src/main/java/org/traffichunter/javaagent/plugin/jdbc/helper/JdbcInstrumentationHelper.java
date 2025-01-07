package org.traffichunter.javaagent.plugin.jdbc.helper;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import org.traffichunter.javaagent.trace.manager.TraceManager;
import org.traffichunter.javaagent.trace.manager.TraceManager.SpanScope;

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
