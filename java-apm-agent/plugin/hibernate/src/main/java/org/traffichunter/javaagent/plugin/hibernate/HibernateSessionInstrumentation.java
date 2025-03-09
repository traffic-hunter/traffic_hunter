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

import static net.bytebuddy.matcher.ElementMatchers.any;
import static net.bytebuddy.matcher.ElementMatchers.hasSuperType;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.namedOneOf;
import static net.bytebuddy.matcher.ElementMatchers.returns;
import static net.bytebuddy.matcher.ElementMatchers.takesArgument;
import static org.traffichunter.javaagent.plugin.hibernate.helper.EntityNameHooker.bestGuessEntityName;
import static org.traffichunter.javaagent.plugin.hibernate.helper.EntityNameHooker.ejectEntityName;

import io.opentelemetry.context.Context;
import jakarta.persistence.criteria.CriteriaQuery;
import java.util.List;
import net.bytebuddy.asm.Advice.Argument;
import net.bytebuddy.asm.Advice.Enter;
import net.bytebuddy.asm.Advice.OnMethodEnter;
import net.bytebuddy.asm.Advice.OnMethodExit;
import net.bytebuddy.asm.Advice.Origin;
import net.bytebuddy.asm.Advice.Return;
import net.bytebuddy.asm.Advice.This;
import net.bytebuddy.asm.Advice.Thrown;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import org.hibernate.SharedSessionContract;
import org.hibernate.Transaction;
import org.hibernate.query.CommonQueryContract;
import org.traffichunter.javaagent.plugin.hibernate.helper.HibernateInstrumentationHelper;
import org.traffichunter.javaagent.plugin.hibernate.helper.SessionInfo;
import org.traffichunter.javaagent.plugin.instrumentation.AbstractPluginInstrumentation;
import org.traffichunter.javaagent.plugin.sdk.field.PluginSupportField;
import org.traffichunter.javaagent.plugin.sdk.instumentation.SpanScope;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
public class HibernateSessionInstrumentation extends AbstractPluginInstrumentation {

    public HibernateSessionInstrumentation() {
        super("hibernate", HibernateSessionInstrumentation.class.getName(), "6.0");
    }

    @Override
    public List<Advice> transform() {
        return List.of(
                Advice.create(
                        ElementMatchers.isMethod()
                                .and(namedOneOf(
                                "save",
                                "replicate",
                                "saveOrUpdate",
                                "update",
                                "merge",
                                "persist",
                                "lock",
                                "fireLock",
                                "refresh",
                                "insert",
                                "delete")
                                .and(takesArgument(0, any()))),
                        Advice.combineClassBinaryPath(HibernateSessionInstrumentation.class, SessionMethodAdvice.class)
                ),

                Advice.create(
                        ElementMatchers.isMethod()
                                .and(namedOneOf("get", "find")
                                .and(returns(Object.class))
                                .and(takesArgument(0, String.class).or(takesArgument(0, Class.class)))),
                        Advice.combineClassBinaryPath(HibernateSessionInstrumentation.class, SessionMethodAdvice.class)
                ),

                Advice.create(
                        ElementMatchers.isMethod()
                                .and(namedOneOf("beginTransaction", "getTransaction")
                                .and(returns(named("org.hibernate.Transaction")))),
                        Advice.combineClassBinaryPath(HibernateSessionInstrumentation.class, GetTransactionAdvice.class)
                ),

                Advice.create(
                        ElementMatchers.isMethod()
                                .and(returns(hasSuperType(named("org.hibernate.query.CommonQueryContract")))
                                .or(named("org.hibernate.query.spi.QueryImplementor"))),
                        Advice.combineClassBinaryPath(HibernateSessionInstrumentation.class, GetQueryAdvice.class)
                )
        );
    }

    @Override
    public ElementMatcher<TypeDescription> typeMatcher() {
        return hasSuperType(named("org.hibernate.SharedSessionContract"));
    }

    @Override
    protected ElementMatcher<? super MethodDescription> isMethod() {
        return null;
    }

    @SuppressWarnings("unused")
    public static class SessionMethodAdvice {

        @OnMethodEnter(suppress = Throwable.class)
        public static SpanScope enter(@This final SharedSessionContract session,
                                      @Origin("#m") final String name,
                                      @Origin("#d") final String descriptor,
                                      @Argument(0) final Object arg0,
                                      @Argument(value = 1, optional = true) final Object arg1) {

            PluginSupportField<SharedSessionContract, SessionInfo> field =
                    PluginSupportField.find(SharedSessionContract.class, SessionInfo.class);

            SessionInfo sessionInfo = field.get(session);

            Context parentContext = Context.current();

            String entityName = ejectEntityName(descriptor, arg0, arg1, bestGuessEntityName(session));

            return HibernateInstrumentationHelper.SessionHelper.start(
                    name,
                    entityName,
                    parentContext,
                    sessionInfo
            );
        }

        @OnMethodExit(suppress = Throwable.class)
        public static void exit(@Enter final SpanScope spanScope, @Thrown Throwable throwable) {

            HibernateInstrumentationHelper.end(spanScope, throwable);
        }
    }

    @SuppressWarnings("unused")
    public static class GetQueryAdvice {

        @OnMethodExit(inline = false, suppress = Throwable.class)
        public static void exit(@This final SharedSessionContract session, @Return final Object queryObject) {

            if(!(queryObject instanceof CommonQueryContract)) {
                return;
            }

            CommonQueryContract query = (CommonQueryContract) queryObject;

            PluginSupportField<SharedSessionContract, SessionInfo> sessionField =
                    PluginSupportField.find(SharedSessionContract.class, SessionInfo.class);

            PluginSupportField<CommonQueryContract, SessionInfo> queryObjectField =
                    PluginSupportField.find(CommonQueryContract.class, SessionInfo.class);

            queryObjectField.set(query, sessionField.get(session));
        }
    }

    @SuppressWarnings("unused")
    public static class GetTransactionAdvice {

        @OnMethodExit(suppress = Throwable.class)
        public static void exit(@This final SharedSessionContract session, @Return final Transaction transaction) {

            PluginSupportField<SharedSessionContract, SessionInfo> sessionField =
                    PluginSupportField.find(SharedSessionContract.class, SessionInfo.class);

            PluginSupportField<Transaction, SessionInfo> transactionField =
                    PluginSupportField.find(Transaction.class, SessionInfo.class);

            transactionField.set(transaction, sessionField.get(session));
        }
    }

    @SuppressWarnings("unused")
    public static class GetCriteriaAdvice {

        @OnMethodExit(suppress = Throwable.class)
        public static void exit(@This final SharedSessionContract session, @Return final CriteriaQuery<?> query) {

            PluginSupportField<SharedSessionContract, SessionInfo> sessionField =
                    PluginSupportField.find(SharedSessionContract.class, SessionInfo.class);

            PluginSupportField<CriteriaQuery<?>, SessionInfo> criteriaField =
                    PluginSupportField.find(CriteriaQuery.class, SessionInfo.class);

            criteriaField.set(query, sessionField.get(session));
        }
    }
}
