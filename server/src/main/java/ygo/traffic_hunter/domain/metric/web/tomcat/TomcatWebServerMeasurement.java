package ygo.traffic_hunter.domain.metric.web.tomcat;

import ygo.traffic_hunter.domain.metric.web.tomcat.request.TomcatWebServerRequestMeasurement;
import ygo.traffic_hunter.domain.metric.web.tomcat.thread.TomcatWebServerThreadPoolMeasurement;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public record TomcatWebServerMeasurement(
        TomcatWebServerRequestMeasurement tomcatWebServerRequestMeasurement,
        TomcatWebServerThreadPoolMeasurement tomcatWebServerThreadPoolMeasurement
) {
}
