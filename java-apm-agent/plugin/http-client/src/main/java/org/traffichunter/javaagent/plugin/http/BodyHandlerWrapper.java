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

import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodySubscriber;
import java.net.http.HttpResponse.ResponseInfo;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Flow;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
final class BodyHandlerWrapper<T> implements BodyHandler<T>{

    private final BodyHandler<T> delegate;
    private final Context context;

    BodyHandlerWrapper(final BodyHandler<T> delegate, final Context context) {
        this.delegate = delegate;
        this.context = context;
    }

    @Override
    public BodySubscriber<T> apply(final ResponseInfo responseInfo) {
        BodySubscriber<T> subscriber = delegate.apply(responseInfo);
        if(subscriber instanceof BodySubscriberWrapper) {
            return subscriber;
        }

        return new BodySubscriberWrapper<>(subscriber, context);
    }

    static class BodySubscriberWrapper<T> implements BodySubscriber<T> {
        private final BodySubscriber<T> delegate;
        private final Context context;

        BodySubscriberWrapper(final BodySubscriber<T> delegate, final Context context) {
            this.delegate = delegate;
            this.context = context;
        }

        public BodySubscriber<T> getDelegate() {
            return delegate;
        }

        @Override
        public CompletionStage<T> getBody() {
            return delegate.getBody();
        }

        @Override
        public void onSubscribe(final Flow.Subscription subscription) {
            delegate.onSubscribe(subscription);
        }

        @Override
        public void onNext(final List<ByteBuffer> item) {
            try (Scope ignore = context.makeCurrent()) {
                delegate.onNext(item);
            }
        }

        @Override
        public void onError(final Throwable throwable) {
            try (Scope ignore = context.makeCurrent()) {
                delegate.onError(throwable);
            }
        }

        @Override
        public void onComplete() {
            try (Scope ignore = context.makeCurrent()) {
                delegate.onComplete();
            }
        }
    }
}
