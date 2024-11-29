package ygo.traffichunter.agent.engine.collect.web.tomcat;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.ObjectName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ygo.traffichunter.agent.engine.collect.AbstractMBeanMetricCollector;
import ygo.traffichunter.agent.engine.metric.web.tomcat.TomcatWebServerInfo;
import ygo.traffichunter.agent.engine.metric.web.tomcat.request.TomcatRequestInfo;
import ygo.traffichunter.agent.engine.metric.web.tomcat.thread.TomcatThreadPoolInfo;
import ygo.traffichunter.agent.property.TrafficHunterAgentProperty;

public class TomcatMetricCollector extends AbstractMBeanMetricCollector<TomcatWebServerInfo> {

    private static final Logger log = LoggerFactory.getLogger(TomcatMetricCollector.class.getName());

    private final TrafficHunterAgentProperty property;

    public TomcatMetricCollector(final TrafficHunterAgentProperty property) {
        this.property = property;
    }

    @Override
    public TomcatWebServerInfo collect() {

        try {
            ObjectName threadPoolMbean = new ObjectName("Tomcat:type=ThreadPool,name=" + getPort());
            ObjectName requestMBean = new ObjectName("Tomcat:type=GlobalRequestProcessor,name=" + getPort());

            int maxThreads = getAttribute(threadPoolMbean, "maxThreads", Integer.class);
            int currentThreads = getAttribute(threadPoolMbean, "currentThreadCount", Integer.class);
            int currentThreadsBusy = getAttribute(threadPoolMbean, "currentThreadsBusy", Integer.class);

            int requestCount = getAttribute(requestMBean, "requestCount", Integer.class);
            long bytesReceived = getAttribute(requestMBean, "bytesReceived", Long.class);
            long bytesSent = getAttribute(requestMBean, "bytesSent", Long.class);
            long processingTime = getAttribute(requestMBean, "processingTime", Long.class);
            int errorCount = getAttribute(requestMBean, "errorCount", Integer.class);

            return new TomcatWebServerInfo(
                    new TomcatThreadPoolInfo(maxThreads, currentThreads, currentThreadsBusy),
                    new TomcatRequestInfo(requestCount, bytesReceived, bytesSent, processingTime, errorCount)
            );
        } catch (Exception e) {
            log.error("Failed to collect metrics = {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * target uri -> localhost:8080
     * <br/>
     * translation -> localhost:8080 -> http-nio-8080
     * @return port
     */
    private String getPort() {
        final String port = property.targetUri().split(":")[1];
        return String.format("\"http-nio-%s\"", port);
    }
}
