package ygo.traffichunter.agent.engine.collect;

import java.lang.management.ManagementFactory;
import java.util.Set;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

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
