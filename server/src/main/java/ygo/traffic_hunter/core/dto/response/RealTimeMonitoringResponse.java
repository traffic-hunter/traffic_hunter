package ygo.traffic_hunter.core.dto.response;

import java.util.List;

/**
 * @author JuSeong
 * @version 1.1.0
 */

public record RealTimeMonitoringResponse(List<SystemMetricResponse> systemMetricResponses,
                                         List<TransactionMetricResponse> transactionMetricResponses) {
}
