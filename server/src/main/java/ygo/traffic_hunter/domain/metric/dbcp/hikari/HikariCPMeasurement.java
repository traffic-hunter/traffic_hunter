package ygo.traffic_hunter.domain.metric.dbcp.hikari;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public record HikariCPMeasurement(
        // Connection Status
        int activeConnections,      // Number of active connections currently in use
        int idleConnections,        // Number of idle connections in the pool
        int totalConnections,       // Total number of connections in the pool
        int threadsAwaitingConnection  // Number of threads waiting for a connection
) {
}
