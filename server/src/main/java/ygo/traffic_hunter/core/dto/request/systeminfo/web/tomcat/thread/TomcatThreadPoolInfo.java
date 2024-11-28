package ygo.traffic_hunter.core.dto.request.systeminfo.web.tomcat.thread;

public record TomcatThreadPoolInfo(
        int maxThreads,
        int currentThreads,
        int currentThreadsBusy
) {
}
