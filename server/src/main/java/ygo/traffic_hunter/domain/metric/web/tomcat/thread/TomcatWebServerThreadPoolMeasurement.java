package ygo.traffic_hunter.domain.metric.web.tomcat.thread;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public record TomcatWebServerThreadPoolMeasurement(
        int maxThreads,
        int currentThreads,
        int currentThreadsBusy
) {
}
