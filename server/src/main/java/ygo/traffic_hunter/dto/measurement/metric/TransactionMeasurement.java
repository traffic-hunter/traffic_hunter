package ygo.traffic_hunter.dto.measurement.metric;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import java.time.Instant;

@Measurement(name = "txMetric")
public record TransactionMeasurement(

        @Column(name = "tx_name")
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
