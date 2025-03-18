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
import static net.bytebuddy.matcher.ElementMatchers.namedOneOf;
import static net.bytebuddy.matcher.ElementMatchers.returns;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;

import net.bytebuddy.asm.Advice.OnMethodExit;
import net.bytebuddy.asm.Advice.Return;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import org.hibernate.SharedSessionContract;
import org.traffichunter.javaagent.extension.AbstractPluginInstrumentation;
import org.traffichunter.javaagent.extension.Transformer;
import org.traffichunter.javaagent.plugin.hibernate.helper.SessionInfo;
import org.traffichunter.javaagent.plugin.sdk.field.PluginSupportField;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
public class HibernateSessionFactoryInstrumentation extends AbstractPluginInstrumentation {

    public HibernateSessionFactoryInstrumentation() {
        super("hibernate", HibernateSessionFactoryInstrumentation.class.getName(), "6.0");
    }

    @Override
    public void transform(final Transformer transformer) {

        transformer.processAdvice(
                Advices.create(
                        isMethod(),
                        SessionFactoryAdvice.class
                )
        );
    }

    @Override
    public ElementMatcher<? super TypeDescription> typeMatcher() {
        return hasSuperType(namedOneOf("org.hibernate.SessionFactory", "org.hibernate.SessionBuilder"));
    }

    @Override
    protected ElementMatcher<? super MethodDescription> isMethod() {
        return ElementMatchers.isMethod()
                .and(namedOneOf("openSession", "openStatelessSession"))
                .and(takesArguments(0))
                .and(returns(
                        namedOneOf(
                                "org.hibernate.Session",
                                "org.hibernate.StatelessSession",
                                "org.hibernate.internal.SessionImpl")
                        )
                );
    }

    @SuppressWarnings("unused")
    public static class SessionFactoryAdvice {

        @OnMethodExit(suppress = Throwable.class)
        public static void exit(@Return final SharedSessionContract session) {

            PluginSupportField<SharedSessionContract, SessionInfo> field =
                    PluginSupportField.find(SharedSessionContract.class, SessionInfo.class);

            field.set(session, new SessionInfo());
        }
    }
}
