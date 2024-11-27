package ygo.traffichunter.agent.engine.collect.web.tomcat;

import java.lang.management.ManagementFactory;
import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ygo.traffichunter.agent.engine.collect.AbstractMBeanMetricCollector;
import ygo.traffichunter.agent.engine.collect.MetricCollector;
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
            ObjectName threadPoolMbean = new ObjectName("Catalina:type=ThreadPool,port=" + getPort());
            ObjectName requestMBean = new ObjectName("Catalina:type=GlobalRequestProcessor,port=" + getPort());
            
            int maxThreads = getAttribute(threadPoolMbean, "maxThreads", Integer.class);
            int currentThreads = getAttribute(threadPoolMbean, "currentThreads", Integer.class);
            int currentThreadsBusy = getAttribute(threadPoolMbean, "currentThreadsBusy", Integer.class);

            long requestCount = getAttribute(requestMBean, "requestCount", Long.class);
            long bytesReceived = getAttribute(requestMBean, "bytesReceived", Long.class);
            long bytesSent = getAttribute(requestMBean, "bytesSent", Long.class);
            long processingTime = getAttribute(requestMBean, "processingTime", Long.class);
            long errorCount = getAttribute(requestMBean, "errorCount", Long.class);

            return new TomcatWebServerInfo(
                    new TomcatThreadPoolInfo(maxThreads, currentThreads, currentThreadsBusy),
                    new TomcatRequestInfo(requestCount, bytesReceived, bytesSent, processingTime, errorCount)
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * target uri -> localhost:8080
     * @return port
     */
    private String getPort() {
        return property.targetUri().split(":")[1];
    }

    private ObjectInstance getObjectInstance(final ObjectName objectName) {
        return mBeanServer.queryMBeans(objectName, null).stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No thread pool mbean found"));
    }
}
