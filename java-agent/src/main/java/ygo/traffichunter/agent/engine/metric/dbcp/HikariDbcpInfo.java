package ygo.traffichunter.agent.engine.metric.dbcp;

public record HikariDbcpInfo(
     // Connection Status
     int activeConnections,      // Number of active connections currently in use
     int idleConnections,        // Number of idle connections in the pool
     int totalConnections,       // Total number of connections in the pool
     int threadsAwaitingConnection  // Number of threads waiting for a connection
) {
}
