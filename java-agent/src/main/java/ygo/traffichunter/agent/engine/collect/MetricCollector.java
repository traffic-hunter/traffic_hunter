package ygo.traffichunter.agent.engine.collect;

import java.util.List;
import javax.management.MBeanServerConnection;

/**
 * The {@code MetricCollector} interface defines a contract for collecting metrics.
 * Implementations can collect metrics from various sources, such as the local JVM
 * or an MBeanServer.
 *
 * <p>Features:</p>
 * <ul>
 *     <li>Provides methods for collecting a single metric or all available metrics.</li>
 *     <li>Includes default implementations for optional operations.</li>
 * </ul>
 *
 * @param <T> The type of metric being collected.
 *
 * @author yungwang-o
 * @version 1.0.0
 */
public interface MetricCollector<T> {

    default T collect(MBeanServerConnection mbsc) {
        return null;
    }

    T collect();

    default List<T> collectAll() {return null;}
}
