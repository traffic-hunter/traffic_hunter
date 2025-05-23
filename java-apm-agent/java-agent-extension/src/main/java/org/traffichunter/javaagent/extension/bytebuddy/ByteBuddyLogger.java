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
package org.traffichunter.javaagent.extension.bytebuddy;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.bytebuddy.agent.builder.AgentBuilder.Listener;
import net.bytebuddy.agent.builder.AgentBuilder.RedefinitionStrategy;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.utility.JavaModule;
import org.traffichunter.javaagent.bootstrap.BootstrapLogger;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
public class ByteBuddyLogger {

    public static class RedefinitionStrategyLoggingAdapter implements RedefinitionStrategy.Listener {

        private static final BootstrapLogger log = BootstrapLogger.getLogger(RedefinitionStrategyLoggingAdapter.class);

        @Override
        public void onBatch(final int i, final List<Class<?>> list, final List<Class<?>> list1) {}

        @Override
        public Iterable<? extends List<Class<?>>> onError(final int i,
                                                          final List<Class<?>> list,
                                                          final Throwable throwable,
                                                          final List<Class<?>> list1) {

            return Collections.emptyList();
        }

        @Override
        public void onComplete(final int i, final List<Class<?>> list, final Map<List<Class<?>>, Throwable> map) {}
    }

    public static class TransformLoggingListenAdapter extends Listener.Adapter {

        private static final BootstrapLogger log = BootstrapLogger.getLogger(TransformLoggingListenAdapter.class);

        @Override
        public void onError(final String typeName,
                            final ClassLoader classLoader,
                            final JavaModule module,
                            final boolean loaded,
                            final Throwable throwable) {

            log.error("Failed to handle for transformation on class loader = {} {}", typeName, throwable);
        }

        @Override
        public void onComplete(final String typeName,
                               final ClassLoader classLoader,
                               final JavaModule module,
                               final boolean loaded) {
        }

        @Override
        public void onTransformation(final TypeDescription typeDescription,
                                     final ClassLoader classLoader,
                                     final JavaModule module,
                                     final boolean loaded,
                                     final DynamicType dynamicType) {

            log.info("Transforming - {}, cl: {}, loaded: {}", typeDescription.getName(), classLoader, loaded);
        }
    }

    public static class ClassLoaderLoggingAdapter extends Listener.Adapter {

        @Override
        public void onComplete(final String typeName,
                               final ClassLoader classLoader,
                               final JavaModule module,
                               final boolean loaded) {


        }
    }
}
