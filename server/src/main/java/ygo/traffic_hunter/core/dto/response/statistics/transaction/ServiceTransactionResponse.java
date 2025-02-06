package ygo.traffic_hunter.core.dto.response.statistics.transaction;

public record ServiceTransactionResponse(

        String url,

        long count,

        long errCount,

        double avgExecutionTime,

        long sumExecutionTime,

        long maxExecutionTime
) {
}
