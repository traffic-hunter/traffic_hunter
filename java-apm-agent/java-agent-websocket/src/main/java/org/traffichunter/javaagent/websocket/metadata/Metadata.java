package org.traffichunter.javaagent.websocket.metadata;

import java.time.Instant;
import org.traffichunter.javaagent.commons.status.AgentStatus;

public record Metadata(
        String agentId,
        String agentVersion,
        String agentName,
        Instant startTime,
        AgentStatus status) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String agentId;
        private String agentVersion;
        private String agentName;
        private Instant startTime;
        private AgentStatus status;

        public Builder agentId(String agentId) {
            this.agentId = agentId;
            return this;
        }

        public Builder agentVersion(String agentVersion) {
            this.agentVersion = agentVersion;
            return this;
        }

        public Builder agentName(String agentName) {
            this.agentName = agentName;
            return this;
        }

        public Builder startTime(Instant startTime) {
            this.startTime = startTime;
            return this;
        }

        public Builder status(AgentStatus status) {
            this.status = status;
            return this;
        }

        public Metadata build() {
            return new Metadata(agentId, agentVersion, agentName, startTime, status);
        }
    }
}
