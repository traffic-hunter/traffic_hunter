package ygo.traffic_hunter.domain.metric.web.tomcat.thread;

public record TomcatWebServerThreadPoolMeasurement(
        int maxThreads,
        int currentThreads,
        int currentThreadsBusy
) {
}
