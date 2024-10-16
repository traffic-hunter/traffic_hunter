package ygo.traffichunter.agent.engine.collect;

import javax.management.MBeanServerConnection;

public interface MetricCollector<T> {
    T collect(MBeanServerConnection mbsc);
}
