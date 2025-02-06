package ygo.traffic_hunter.core.dto.request.statistics;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record StatisticsRequest(@NotNull Instant begin, Instant end) {
}
