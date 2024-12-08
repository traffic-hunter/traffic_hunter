package ygo.traffichunter.agent.engine.metric.web.tomcat;

import ygo.traffichunter.agent.engine.metric.web.tomcat.request.TomcatRequestInfo;
import ygo.traffichunter.agent.engine.metric.web.tomcat.thread.TomcatThreadPoolInfo;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public record TomcatWebServerInfo(
        TomcatThreadPoolInfo tomcatThreadPoolInfo,
        TomcatRequestInfo tomcatRequestInfo
) {
}
