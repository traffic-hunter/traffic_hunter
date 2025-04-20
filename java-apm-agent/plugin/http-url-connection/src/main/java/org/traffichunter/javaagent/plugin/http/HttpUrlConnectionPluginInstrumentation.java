/*
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
package org.traffichunter.javaagent.plugin.http;

import static net.bytebuddy.matcher.ElementMatchers.hasSuperClass;
import static net.bytebuddy.matcher.ElementMatchers.isPublic;
import static net.bytebuddy.matcher.ElementMatchers.nameStartsWith;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.namedOneOf;
import static net.bytebuddy.matcher.ElementMatchers.not;

import io.opentelemetry.context.Context;
import java.net.HttpURLConnection;
import net.bytebuddy.asm.Advice.Enter;
import net.bytebuddy.asm.Advice.FieldValue;
import net.bytebuddy.asm.Advice.OnMethodEnter;
import net.bytebuddy.asm.Advice.OnMethodExit;
import net.bytebuddy.asm.Advice.This;
import net.bytebuddy.asm.Advice.Thrown;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import org.traffichunter.javaagent.extension.AbstractPluginInstrumentation;
import org.traffichunter.javaagent.extension.Transformer;
import org.traffichunter.javaagent.plugin.sdk.instumentation.SpanScope;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
public class HttpUrlConnectionPluginInstrumentation extends AbstractPluginInstrumentation {

    public HttpUrlConnectionPluginInstrumentation() {
        super("http-url-connection", HttpUrlConnectionPluginInstrumentation.class.getName(), "");
    }

    @Override
    public void transform(final Transformer transformer) {

        transformer.processAdvice(
                Advices.create(
                        ElementMatchers.isMethod()
                                .and(isPublic())
                                .and(namedOneOf("connect", "getOutputStream", "getInputStream")),
                        HttpUrlConnectionAdvice.class
                )
        );
    }

    @Override
    public ElementMatcher<? super TypeDescription> typeMatcher() {
        return nameStartsWith("java.net")
                .or(nameStartsWith("sun.net"))
                .or(named("weblogic.net.http.HttpURLConnection"))
                .and(not(named("sun.net.www.protocol.https.HttpsURLConnectionImpl")))
                .and(hasSuperClass(named("java.net.HttpURLConnection")));
    }

    @Override
    protected ElementMatcher<? super MethodDescription> isMethod() {
        return null;
    }

    @SuppressWarnings("unused")
    public static class HttpUrlConnectionAdvice {

        @OnMethodEnter(suppress = Throwable.class)
        public static SpanScope enter(@This final HttpURLConnection httpURLConnection,
                                      @FieldValue("connected") boolean connected) {

            Context context = Context.current();

            return HttpUrlConnectionInstrumentationHelper.start(httpURLConnection, connected, context);
        }

        @OnMethodExit(suppress = Throwable.class, onThrowable = Throwable.class)
        public static void exit(@Enter final SpanScope spanScope, @Thrown final Throwable throwable) {

            HttpUrlConnectionInstrumentationHelper.end(spanScope, throwable);
        }
    }
}
