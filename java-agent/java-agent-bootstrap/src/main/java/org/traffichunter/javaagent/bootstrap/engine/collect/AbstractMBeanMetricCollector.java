/**
 * The MIT License
 *
 * Copyright (c) 2024 yungwang-o
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
package org.traffichunter.javaagent.bootstrap.engine.collect;

import java.lang.management.ManagementFactory;
import java.util.Set;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

/**
 * The {@code AbstractMBeanMetricCollector} abstract class provides a base implementation
 * for collecting metrics using the JMX {@link MBeanServer}.
 *
 * <p>Features:</p>
 * <ul>
 *     <li>Extends {@link MetricCollector} for metric collection.</li>
 *     <li>Provides helper methods to query MBeans and retrieve attributes.</li>
 *     <li>Handles exceptions gracefully through the nested {@link MetricCollectionException} class.</li>
 * </ul>
 *
 * @see MetricCollector
 * @see MBeanServer
 *
 * @author yungwang-o
 * @version 1.0.0
 */
public abstract class AbstractMBeanMetricCollector<T> implements MetricCollector<T> {

    protected final MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();

    protected Set<ObjectName> findObjectName(final String objectName) {
        try {
            return mBeanServer.queryNames(new ObjectName(objectName), null);
        } catch (MalformedObjectNameException e) {
            throw new RuntimeException(e);
        }
    }

    protected <A> A getAttribute(final ObjectName name, final String attribute, final Class<A> type) {
        try {

            return type.cast(mBeanServer.getAttribute(name, attribute));
        } catch (Exception e) {
            throw new MetricCollectionException("Failed to get attribute: " + attribute, e);
        }
    }

    public static class MetricCollectionException extends RuntimeException {

        public MetricCollectionException() {
            super();
        }

        public MetricCollectionException(final String message) {
            super(message);
        }

        public MetricCollectionException(final String message, final Throwable cause) {
            super(message, cause);
        }

        public MetricCollectionException(final Throwable cause) {
            super(cause);
        }

        protected MetricCollectionException(final String message, final Throwable cause,
                                            final boolean enableSuppression,
                                            final boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }
}
