package ygo.traffichunter.agent.engine.collect;

import javax.management.MBeanServerConnection;

public interface MetricCollector<T> {

    default T collect(MBeanServerConnection mbsc) {
        return null;
    }

    default T collect() {return null;}
}
