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
import static net.bytebuddy.matcher.ElementMatchers.not;
import static net.bytebuddy.matcher.ElementMatchers.takesArgument;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;

import io.opentelemetry.context.Context;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.util.concurrent.CompletableFuture;
import net.bytebuddy.asm.Advice.Argument;
import net.bytebuddy.asm.Advice.Enter;
import net.bytebuddy.asm.Advice.OnMethodEnter;
import net.bytebuddy.asm.Advice.OnMethodExit;
import net.bytebuddy.asm.Advice.Return;
import net.bytebuddy.asm.Advice.Thrown;
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
public class HttpClientPluginInstrumentation extends AbstractPluginInstrumentation {

    public HttpClientPluginInstrumentation() {
        super("http-client", HttpClientPluginInstrumentation.class.getName(), "");
    }

    @Override
    public void transform(final Transformer transformer) {

        transformer.processAdvice(
                Advices.create(
                    ElementMatchers.isMethod()
                            .and(named("send"))
                            .and(isPublic())
                            .and(takesArguments(2))
                            .and(takesArgument(0, named("java.net.http.HttpRequest"))),
                    SendAdvice.class
                )
        );

        transformer.processAdvice(
                Advices.create(
                    ElementMatchers.isMethod()
                            .and(named("sendAsync"))
                            .and(isPublic())
                            .and(takesArgument(0, named("java.net.http.HttpRequest")))
                            .and(takesArgument(1, named("java.net.http.HttpResponse$BodyHandler"))),
                    AsyncSendAdvice.class
                )
        );
    }

    @Override
    public ElementMatcher<? super TypeDescription> typeMatcher() {

        return nameStartsWith("java.net")
                .or(nameStartsWith("jdk.internal"))
                .and(not(named("jdk.internal.net.http.HttpClientFacade")))
                .and(hasSuperClass(named("java.net.http.HttpClient")));
    }

    @SuppressWarnings("unused")
    public static class SendAdvice {

        @OnMethodEnter(suppress = Throwable.class)
        public static SpanScope enter(@Argument(0) final HttpRequest request) {

            Context context = Context.current();

            return HttpClientInstrumentationHelper.start(request, context);
        }

        @OnMethodExit(suppress = Throwable.class, onThrowable = Throwable.class)
        public static void end(@Enter final SpanScope spanScope,
                               @Return final HttpResponse<?> response,
                               @Thrown final Throwable throwable) {

            HttpClientInstrumentationHelper.end(spanScope, response, throwable);
        }
    }

    @SuppressWarnings("unused")
    public static class AsyncSendAdvice {

        @OnMethodEnter(suppress = Throwable.class)
        public static SpanScope enter(@Argument(value = 0) final HttpRequest httpRequest,
                                      @Argument(value = 1, readOnly = false) BodyHandler<?> bodyHandler) {

            Context context = Context.current();

            if(bodyHandler != null) {
                bodyHandler = new BodyHandlerWrapper<>(bodyHandler, context);
            }

            return HttpClientInstrumentationHelper.start(httpRequest, context);
        }

        @OnMethodExit(suppress = Throwable.class, onThrowable = Throwable.class)
        public static void end(@Argument(0) final HttpRequest httpRequest,
                               @Return(readOnly = false) CompletableFuture<HttpResponse<?>> future,
                               @Thrown final Throwable throwable,
                               @Enter final SpanScope spanScope) {

            if(throwable != null) {
                HttpClientInstrumentationHelper.end(spanScope, null, throwable);
            } else {
                future = future.whenComplete(new ResponseBiConsumer(spanScope));
                future = CompletableFutureWrapper.wrap(future, spanScope);
            }
        }
    }
}
