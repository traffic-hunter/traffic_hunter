package ygo.traffichunter.agent.engine.collect.dbcp.hikari;

import java.time.Instant;
import javax.management.ObjectName;
import ygo.traffichunter.agent.engine.collect.AbstractMBeanMetricCollector;
import ygo.traffichunter.agent.engine.metric.dbcp.HikariDbcpInfo;

public class HikariCPMetricCollector extends AbstractMBeanMetricCollector<HikariDbcpInfo> {

    @Override
    public HikariDbcpInfo collect() {
        try {

            //The default name for a Hikari connection pool is "HikariPool-1"
            ObjectName hikariInfo = new ObjectName("com.zaxxer.hikari:type=Pool (HikariPool-1)");

            int activeConnections = getAttribute(hikariInfo, "ActiveConnections", Integer.class);
            int idleConnections = getAttribute(hikariInfo, "IdleConnections", Integer.class);
            int totalConnections = getAttribute(hikariInfo, "TotalConnections", Integer.class);
            int threadsAwaitingConnections = getAttribute(hikariInfo, "ThreadsAwaitingConnection", Integer.class);

            return new HikariDbcpInfo(
                    Instant.now(),
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
