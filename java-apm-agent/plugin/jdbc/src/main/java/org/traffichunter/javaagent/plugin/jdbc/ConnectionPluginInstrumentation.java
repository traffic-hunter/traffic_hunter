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

import java.sql.PreparedStatement;
import java.util.Collections;
import java.util.List;
import net.bytebuddy.asm.Advice.Argument;
import net.bytebuddy.asm.Advice.OnMethodExit;
import net.bytebuddy.asm.Advice.Return;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import org.traffichunter.javaagent.plugin.instrumentation.AbstractPluginInstrumentation;
import org.traffichunter.javaagent.plugin.jdbc.library.JdbcData;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
public class ConnectionPluginInstrumentation extends AbstractPluginInstrumentation {

    public ConnectionPluginInstrumentation() {
        super("jdbc", ConnectionPluginInstrumentation.class.getName(), "");
    }

    @Override
    public List<Advice> transform() {
        return Collections.singletonList(
                Advice.create(
                isMethod(),
                Advice.combineClassBinaryPath(ConnectionPluginInstrumentation.class, ConnectionAdvice.class)
        ));
    }

    @Override
    public ElementMatcher<TypeDescription> typeMatcher() {
        return hasSuperType(named("java.sql.Connection"));
    }

    @Override
    protected ElementMatcher<? super MethodDescription> isMethod() {
        return nameStartsWith("prepare")
                .and(takesArgument(0, String.class))
                .and(returns(hasSuperType(named("java.sql.PreparedStatement"))));
    }

    @SuppressWarnings("unused")
    public static class ConnectionAdvice {

        @OnMethodExit(suppress = Throwable.class)
        public static void dbInfo(@Argument(0) String sql, @Return PreparedStatement preparedStatement) {

            JdbcData.prepareStatementInfo.set(preparedStatement, sql);
        }
    }
}
