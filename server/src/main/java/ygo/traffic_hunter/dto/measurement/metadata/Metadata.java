package ygo.traffic_hunter.dto.measurement.metadata;

import com.influxdb.annotations.Column;
import java.time.Instant;
import ygo.traffic_hunter.dto.systeminfo.metadata.AgentStatus;

public record Metadata(

        @Column(name = "agent_id", tag = true)
        String agentId,

        @Column(name = "agent_name", tag = true)
        String agentName,

        @Column(name = "agent_version")
        String agentVersion,

        @Column(name = "agent_boot_time")
        Instant agentBootTime,

        @Column(name = "agent_status")
        AgentStatus agentStatus
) {
}
