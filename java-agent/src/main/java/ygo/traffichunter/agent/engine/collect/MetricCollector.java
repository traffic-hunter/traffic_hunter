package ygo.traffichunter.agent.engine.collect;

import java.util.List;
import javax.management.MBeanServerConnection;

public interface MetricCollector<T> {

    default T collect(MBeanServerConnection mbsc) {
        return null;
    }

    default T collect() {return null;}

    default List<T> collectAll() {return null;}
}
