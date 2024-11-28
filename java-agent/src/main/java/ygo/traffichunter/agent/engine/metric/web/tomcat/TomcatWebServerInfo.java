package ygo.traffichunter.agent.engine.metric.web.tomcat;

import ygo.traffichunter.agent.engine.metric.web.tomcat.request.TomcatRequestInfo;
import ygo.traffichunter.agent.engine.metric.web.tomcat.thread.TomcatThreadPoolInfo;

public record TomcatWebServerInfo(
        TomcatThreadPoolInfo tomcatThreadPoolInfo,
        TomcatRequestInfo tomcatRequestInfo
) {
}
