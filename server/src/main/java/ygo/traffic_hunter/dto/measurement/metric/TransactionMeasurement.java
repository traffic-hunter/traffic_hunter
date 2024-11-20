package ygo.traffic_hunter.dto.measurement.metric;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import java.time.Instant;
import ygo.traffic_hunter.dto.measurement.metadata.Metadata;
import ygo.traffic_hunter.dto.systeminfo.metadata.AgentStatus;

@Measurement(name = "txMetric")
public record TransactionMeasurement(

        @Column(name = "agent_id", tag = true)
        String agentId,

        @Column(name = "agent_name", tag = true)
        String agentName,

        @Column(name = "agent_version")
        String agentVersion,

        @Column(name = "agent_boot_time")
        Instant agentBootTime,

        @Column(name = "tx_name", tag = true)
        String txName,

        @Column(name = "start_time", timestamp = true)
        Instant startTime,

        @Column(name = "end_time")
        Instant endTime,

        @Column(name = "duration")
        long duration,

        @Column(name = "error_message")
        String errorMessage,

        @Column(name = "is_success")
        boolean isSuccess
) {
}
