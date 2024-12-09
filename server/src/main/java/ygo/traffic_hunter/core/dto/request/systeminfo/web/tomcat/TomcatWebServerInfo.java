package ygo.traffic_hunter.core.dto.request.systeminfo.web.tomcat;

import ygo.traffic_hunter.core.dto.request.systeminfo.web.tomcat.request.TomcatRequestInfo;
import ygo.traffic_hunter.core.dto.request.systeminfo.web.tomcat.thread.TomcatThreadPoolInfo;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public record TomcatWebServerInfo(
        TomcatThreadPoolInfo tomcatThreadPoolInfo,
        TomcatRequestInfo tomcatRequestInfo
) {
}
