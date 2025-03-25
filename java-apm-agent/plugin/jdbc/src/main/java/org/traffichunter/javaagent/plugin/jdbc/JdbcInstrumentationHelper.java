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
package org.traffichunter.javaagent.plugin.jdbc;

import io.opentelemetry.context.Context;
import org.traffichunter.javaagent.plugin.jdbc.library.DatabaseRequest;
import org.traffichunter.javaagent.plugin.sdk.instumentation.Instrumentor;
import org.traffichunter.javaagent.plugin.sdk.instumentation.SpanScope;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
public class JdbcInstrumentationHelper {

    public static class StatementInstrumentation {

        public static SpanScope start(final DatabaseRequest request, final Context parentContext) {
            return JdbcInstrumentationHelper.start(
                    request,
                    parentContext,
                    "jdbc-statement-inst"
            );
        }
    }

    public static class PreparedStatementInstrumentation {

        public static SpanScope start(final Context parentContext, final DatabaseRequest request) {
            return JdbcInstrumentationHelper.start(
                    request,
                    parentContext,
                    "jdbc-prepared-statement-inst"
            );
        }
    }

    private static SpanScope start(final DatabaseRequest request,
                                   final Context parentContext,
                                   final String instrumentationName) {

        return Instrumentor.startBuilder(request)
                .spanName(DatabaseRequest::getStatementString)
                .context(parentContext)
                .instrumentationName(instrumentationName)
                .spanAttribute((span, req) ->
                        span.setAttribute("sql", req.getStatementString())
                                .setAttribute("system", req.getDatabaseInfo().getSystem())
                                .setAttribute("user", req.getDatabaseInfo().getUser())
                                .setAttribute("url", req.getDatabaseInfo().getUrl())
                                .setAttribute("db", req.getDatabaseInfo().getDb())
                                .setAttribute("port", req.getDatabaseInfo().getDb())
                                .setAttribute("host", req.getDatabaseInfo().getHost())
                ).start();
    }

    public static void end(final SpanScope spanScope, final Throwable throwable) {
        Instrumentor.end(spanScope, throwable);
    }
}
