package ygo.traffichunter.agent.engine.collect.dbcp.hikari;

import java.lang.management.ManagementFactory;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import ygo.traffichunter.agent.engine.collect.MetricCollector;
import ygo.traffichunter.agent.engine.metric.dbcp.HikariDbcpInfo;

public class HikariCPMetricCollector implements MetricCollector<HikariDbcpInfo> {

    private static final MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();

    @Override
    public HikariDbcpInfo collect() {
        try {

            //The default name for a Hikari connection pool is "HikariPool-1"
            ObjectName hikariInfo = new ObjectName("com.zaxxer.hikari:type=Pool (HikariPool-1)");

            int activeConnections = (int) mBeanServer.getAttribute(hikariInfo, "ActiveConnections");
            int idleConnections = (int) mBeanServer.getAttribute(hikariInfo, "IdleConnections");
            int totalConnections = (int) mBeanServer.getAttribute(hikariInfo, "TotalConnections");
            int threadsAwaitingConnections = (int) mBeanServer.getAttribute(hikariInfo, "ThreadsAwaitingConnection");

            return new HikariDbcpInfo(
                    activeConnections,
                    idleConnections,
                    totalConnections,
                    threadsAwaitingConnections
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
