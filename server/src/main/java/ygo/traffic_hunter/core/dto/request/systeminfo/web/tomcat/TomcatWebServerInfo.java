package ygo.traffic_hunter.core.dto.request.systeminfo.web.tomcat;

import ygo.traffic_hunter.core.dto.request.systeminfo.web.tomcat.request.TomcatRequestInfo;
import ygo.traffic_hunter.core.dto.request.systeminfo.web.tomcat.thread.TomcatThreadPoolInfo;

public record TomcatWebServerInfo(
        TomcatThreadPoolInfo tomcatThreadPoolInfo,
        TomcatRequestInfo tomcatRequestInfo
) {
}
