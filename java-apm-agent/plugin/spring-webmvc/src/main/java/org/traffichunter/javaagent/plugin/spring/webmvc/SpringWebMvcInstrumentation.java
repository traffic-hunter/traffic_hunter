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
package org.traffichunter.javaagent.plugin.spring.webmvc;

import static net.bytebuddy.matcher.ElementMatchers.named;

import io.opentelemetry.context.Context;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import net.bytebuddy.asm.Advice.Argument;
import net.bytebuddy.asm.Advice.Enter;
import net.bytebuddy.asm.Advice.OnMethodEnter;
import net.bytebuddy.asm.Advice.OnMethodExit;
import net.bytebuddy.asm.Advice.Origin;
import net.bytebuddy.asm.Advice.Thrown;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import org.traffichunter.javaagent.extension.AbstractPluginInstrumentation;
import org.traffichunter.javaagent.extension.Transformer;
import org.traffichunter.javaagent.plugin.sdk.instumentation.SpanScope;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
public class SpringWebMvcInstrumentation extends AbstractPluginInstrumentation {

    public SpringWebMvcInstrumentation() {
        super("spring-webmvc", SpringWebMvcInstrumentation.class.getName(),"spring-webmvc-6.2.0");
    }

    @Override
    public void transform(final Transformer transformer) {

        transformer.processAdvice(
                Advices.create(
                        isMethod(),
                        SpringWebMvcDispatcherServletAdvice.class
                )
        );
    }

    @Override
    public ElementMatcher<TypeDescription> typeMatcher() {
        return named("org.springframework.web.servlet.DispatcherServlet");
    }

    @Override
    protected ElementMatcher<? super MethodDescription> isMethod() {
        return named("doDispatch");
    }

    @SuppressWarnings("unused")
    public static class SpringWebMvcDispatcherServletAdvice {

        @OnMethodEnter(suppress = Throwable.class)
        public static SpanScope enter(@Origin final Method method,
                                      @Argument(0) final HttpServletRequest request,
                                      @Argument(1) final HttpServletResponse response) {

            Context current = Context.current();

            return SpringWebMvcInstrumentationHelper.start(method, current, request, response);
        }

        @OnMethodExit(suppress = Throwable.class, onThrowable = Throwable.class)
        public static void end(@Enter final SpanScope spanScope, @Thrown final Throwable throwable) {
            SpringWebMvcInstrumentationHelper.end(spanScope, throwable);
        }
    }
}
