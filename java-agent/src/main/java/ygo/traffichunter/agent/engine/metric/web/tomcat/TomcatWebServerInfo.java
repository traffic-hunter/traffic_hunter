package ygo.traffichunter.agent.engine.metric.web.tomcat;

import java.time.Instant;
import ygo.traffichunter.agent.engine.metric.web.tomcat.request.TomcatRequestInfo;
import ygo.traffichunter.agent.engine.metric.web.tomcat.thread.TomcatThreadPoolInfo;

public record TomcatWebServerInfo(
        Instant time,
        TomcatThreadPoolInfo tomcatThreadPoolInfo,
        TomcatRequestInfo tomcatRequestInfo
) {
}
