package ygo.traffichunter.agent.engine.metric.web.tomcat.thread;

public record TomcatThreadPoolInfo(
        int maxThreads,
        int currentThreads,
        int currentThreadsBusy
) {
}
