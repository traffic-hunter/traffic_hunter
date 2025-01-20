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

import static net.bytebuddy.matcher.ElementMatchers.hasSuperType;
import static net.bytebuddy.matcher.ElementMatchers.nameStartsWith;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.returns;
import static net.bytebuddy.matcher.ElementMatchers.takesArgument;

import java.sql.Connection;
import java.util.Objects;
import java.util.Properties;
import net.bytebuddy.agent.builder.AgentBuilder.Transformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.asm.Advice.Argument;
import net.bytebuddy.asm.Advice.OnMethodExit;
import net.bytebuddy.asm.Advice.Return;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import org.traffichunter.javaagent.plugin.jdbc.library.DatabaseInfo;
import org.traffichunter.javaagent.plugin.jdbc.library.JdbcData;
import org.traffichunter.javaagent.plugin.jdbc.library.JdbcInformationParser;
import org.traffichunter.javaagent.plugin.sdk.instrumentation.AbstractPluginInstrumentation;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
public class DriverPluginInstrumentation extends AbstractPluginInstrumentation {

    public DriverPluginInstrumentation() {
        super("jdbc", DriverPluginInstrumentation.class.getSimpleName(), "");
    }

    @Override
    public Transformer transform() {
        return ((builder, typeDescription, classLoader, javaModule, protectionDomain) ->
                builder.method(this.isMethod()).intercept(Advice.to(DriverAdvice.class)));
    }

    @Override
    public ElementMatcher<TypeDescription> typeMatcher() {
        return hasSuperType(named("java.sql.Driver"));
    }

    @Override
    protected ElementMatcher<? super MethodDescription> isMethod() {
        return nameStartsWith("connect")
                .and(takesArgument(0, String.class))
                .and(takesArgument(1, Properties.class))
                .and(returns(named("java.sql.Connection")));
    }

    @SuppressWarnings("unused")
    public static class DriverAdvice {

        @OnMethodExit(suppress = Throwable.class)
        public static void dbInfo(@Argument(0) String url,
                                  @Argument(1) Properties info,
                                  @Return Connection conn) {

            if(Objects.isNull(conn)) {
                return;
            }

            DatabaseInfo databaseInfo = JdbcInformationParser.selectDatabase(url, info);
            JdbcData.connectionInfo.set(conn, JdbcData.canonicalize(databaseInfo));
        }
    }
}
