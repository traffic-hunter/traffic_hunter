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
package org.traffichunter.javaagent.plugin.hibernate;

import io.opentelemetry.context.Context;
import org.hibernate.Transaction;
import org.traffichunter.javaagent.plugin.hibernate.helper.SessionInfo;
import org.traffichunter.javaagent.plugin.sdk.instumentation.Instrumentor;
import org.traffichunter.javaagent.plugin.sdk.instumentation.SpanScope;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
public class HibernateInstrumentationHelper {

    public static class TransactionHelper {

        public static SpanScope start(final Transaction transaction,
                                      final Context parentContext,
                                      final SessionInfo sessionInfo) {

            return Instrumentor.builder(transaction)
                    .spanName(tx -> generateHibernateInstrumentName(tx.getClass()))
                    .context(parentContext)
                    .spanAttribute((span, tx) ->
                            span.setAttribute("transaction.commit", sessionInfo.getSessionId())
                            .setAttribute("transaction.status", tx.getStatus().name())
                    ).start();
        }
    }

    public static class SessionHelper {

        public static SpanScope start(final String name,
                                      final String entityName,
                                      final Context parentContext,
                                      final SessionInfo sessionInfo) {

            return Instrumentor.builder(name)
                    .spanName(methodName -> "hibernate-session")
                    .context(parentContext)
                    .spanAttribute(((span, s) ->
                            span.setAttribute("session.method", s)
                            .setAttribute("session.entity.name", entityName)
                            .setAttribute("session.id", sessionInfo.getSessionId()))
                    ).start();
        }
    }

    public static class QueryHelper {

        public static SpanScope start(final String queryStr,
                                      final Context parentContext,
                                      final SessionInfo sessionInfo) {

            return Instrumentor.builder(queryStr)
                    .spanName(methodName -> "hibernate-query")
                    .context(parentContext)
                    .spanAttribute(((span, query) ->
                            span.setAttribute("query", query)
                            .setAttribute("session.id", sessionInfo.getSessionId()))
                    ).start();
        }
    }

    public static void end(final SpanScope spanScope, final Throwable throwable) {
        Instrumentor.end(spanScope, throwable);
    }

    private static String generateHibernateInstrumentName(final Class<?> clazz) {
        return "hibernate-" + clazz.getSimpleName();
    }
}
