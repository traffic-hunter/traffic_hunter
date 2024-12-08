package ygo.traffichunter.agent.engine.metric.web.tomcat.thread;

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
