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

import static net.bytebuddy.matcher.ElementMatchers.hasSuperType;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.namedOneOf;

import io.opentelemetry.context.Context;
import java.util.Collections;
import java.util.List;
import net.bytebuddy.asm.Advice.Enter;
import net.bytebuddy.asm.Advice.OnMethodEnter;
import net.bytebuddy.asm.Advice.OnMethodExit;
import net.bytebuddy.asm.Advice.This;
import net.bytebuddy.asm.Advice.Thrown;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import org.hibernate.query.CommonQueryContract;
import org.hibernate.query.Query;
import org.hibernate.query.spi.SqmQuery;
import org.traffichunter.javaagent.extension.AbstractPluginInstrumentation;
import org.traffichunter.javaagent.extension.Transformer;
import org.traffichunter.javaagent.plugin.hibernate.helper.SessionInfo;
import org.traffichunter.javaagent.plugin.sdk.field.PluginSupportField;
import org.traffichunter.javaagent.plugin.sdk.instumentation.SpanScope;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
public class HibernateQueryInstrumentation extends AbstractPluginInstrumentation {

    public HibernateQueryInstrumentation() {
        super("hibernate", HibernateQueryInstrumentation.class.getName(), "6.0");
    }

    @Override
    public void transform(final Transformer transformer) {

        List<Advice> advice = Collections.singletonList(
                Advice.create(
                        isMethod(),
                        Advice.combineClassBinaryPath(HibernateQueryInstrumentation.class, QueryAdvice.class)
                )
        );

        transformer.processAdvice(advice);
    }

    @Override
    public ElementMatcher<? super TypeDescription> typeMatcher() {
        return hasSuperType(named("org.hibernate.query.CommonQueryContract"));
    }

    @Override
    protected ElementMatcher<? super MethodDescription> isMethod() {
        return ElementMatchers.isMethod()
                .and(namedOneOf(
                        "list",
                        "getResultList",
                        "stream",
                        "getResultStream",
                        "uniqueResult",
                        "getSingleResultOrNull",
                        "uniqueResultOptional",
                        "executeUpdate",
                        "scroll")
                );
    }

    @SuppressWarnings("unused")
    public static class QueryAdvice {

        @OnMethodEnter(suppress = Throwable.class)
        public static SpanScope enter(@This final CommonQueryContract query) {

            String queryStr = null;

            if(query instanceof Query) {
                queryStr = ((Query<?>) query).getQueryString();
            }

            if(query instanceof SqmQuery) {
                try {
                    queryStr = ((SqmQuery) query).getSqmStatement().toHqlString();
                } catch (RuntimeException ignore) {}
            }

            PluginSupportField<CommonQueryContract, SessionInfo> field =
                    PluginSupportField.find(CommonQueryContract.class, SessionInfo.class);

            SessionInfo sessionInfo = field.get(query);

            Context parentContext = Context.current();

            return HibernateInstrumentationHelper.QueryHelper.start(queryStr, parentContext, sessionInfo);
        }

        @OnMethodExit(suppress = Throwable.class, onThrowable = Throwable.class)
        public static void exit(@Enter final SpanScope spanScope, @Thrown Throwable throwable) {

            HibernateInstrumentationHelper.end(spanScope, throwable);
        }
    }
}
