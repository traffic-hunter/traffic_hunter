package ygo.traffichunter.agent.engine.collect.web.tomcat;

import java.lang.management.ManagementFactory;
import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ygo.traffichunter.agent.engine.collect.MetricCollector;
import ygo.traffichunter.agent.engine.metric.web.tomcat.TomcatWebServerInfo;
import ygo.traffichunter.agent.engine.metric.web.tomcat.request.TomcatRequestInfo;
import ygo.traffichunter.agent.engine.metric.web.tomcat.thread.TomcatThreadPoolInfo;
import ygo.traffichunter.agent.property.TrafficHunterAgentProperty;

public class TomcatMetricCollector implements MetricCollector<TomcatWebServerInfo> {

    private static final Logger log = LoggerFactory.getLogger(TomcatMetricCollector.class.getName());

    private static final MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();

    private final TrafficHunterAgentProperty property;

    public TomcatMetricCollector(final TrafficHunterAgentProperty property) {
        this.property = property;
    }

    @Override
    public TomcatWebServerInfo collect() {

        try {
            ObjectName threadPoolMbean = new ObjectName("Catalina:type=ThreadPool,port=" + getPort());
            ObjectName requestMBean = new ObjectName("Catalina:type=GlobalRequestProcessor,port=" + getPort());
            
            int maxThreads = (int) mBeanServer.getAttribute(threadPoolMbean, "maxThreads");
            int currentThreads = (int) mBeanServer.getAttribute(threadPoolMbean, "currentThreads");
            int currentThreadsBusy = (int) mBeanServer.getAttribute(threadPoolMbean, "currentThreadsBusy");

            long requestCount = (long) mBeanServer.getAttribute(requestMBean, "requestCount");
            long bytesReceived = (long) mBeanServer.getAttribute(requestMBean, "bytesReceived");
            long bytesSent = (long) mBeanServer.getAttribute(requestMBean, "bytesSent");
            long processingTime = (long) mBeanServer.getAttribute(requestMBean, "processingTime");
            long errorCount = (long) mBeanServer.getAttribute(requestMBean, "errorCount");

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
