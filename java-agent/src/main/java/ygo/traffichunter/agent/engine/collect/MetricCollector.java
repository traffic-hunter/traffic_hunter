package ygo.traffichunter.agent.engine.collect;

import javax.management.MBeanServerConnection;

public interface MetricCollector<T, CONN> {
    T collect(CONN mbsc);
}
