package ygo.traffic_hunter.core.dto.request.systeminfo.web.tomcat.thread;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public record TomcatThreadPoolInfo(
        int maxThreads,
        int currentThreads,
        int currentThreadsBusy
) {
}
