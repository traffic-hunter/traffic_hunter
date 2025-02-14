package ygo.traffic_hunter.core.dto.response.statistics.transaction;

import java.time.OffsetDateTime;

public record ServiceTransactionResponse(

        OffsetDateTime timestamp,

        String uri,

        long duration,

        String httpMethod,

        String agentName,

        String clientName,

        int httpStatusCode,

        String traceId
) {
}
