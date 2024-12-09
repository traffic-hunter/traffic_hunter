package ygo.traffic_hunter.core.collector;

import java.nio.ByteBuffer;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import ygo.traffic_hunter.core.annotation.Collector;
import ygo.traffic_hunter.core.collector.channel.MetricChannel;
import ygo.traffic_hunter.core.collector.processor.MetricProcessor;
import ygo.traffic_hunter.core.collector.validator.MetricValidator;

/**
 * <p>
 * The {@code MetricCollector} class is responsible for routing and processing
 * metric data using a set of {@link MetricChannel} handlers.
 * </p>
 *
 * <h4>Core Responsibilities</h4>
 * <ul>
 *     <li>Manages a collection of {@link MetricChannel} handlers.</li>
 *     <li>Routes incoming metric data based on its header byte.</li>
 *     <li>Delegates data processing to the appropriate {@link MetricChannel}.</li>
 * </ul>
 *
 * <h4>Key Components</h4>
 * <dl>
 *     <dt>{@link MetricCollector}</dt>
 *     <dd>The main class for managing and routing metric data.</dd>
 *     <dt>{@link MetricChannel}</dt>
 *     <dd>An interface defining methods for handling specific metric types.</dd>
 *     <dt>{@link MetricProcessor}</dt>
 *     <dd>Processes raw data into structured objects like {@code SystemInfo} or {@code TransactionInfo}.</dd>
 *     <dt>{@link MetricValidator}</dt>
 *     <dd>Validates metric metadata to ensure correctness and agent activity.</dd>
 * </dl>
 *
 * <h4>Workflow</h4>
 * <ol>
 *     <li>{@code MetricCollector} receives raw data as a {@link java.nio.ByteBuffer}.</li>
 *     <li>Converts the data into a byte array.</li>
 *     <li>Extracts the header byte to identify the correct {@link MetricChannel}.</li>
 *     <li>Delegates the payload to the {@link MetricChannel#open(byte[])} method for processing.</li>
 * </ol>
 *
 * <h4>Channel Pipeline Flow</h4>
 * <pre>
 * [ByteBuffer]
 *    ↓
 * [Convert to byte[]]
 *    ↓
 * [Header Byte]
 *    ↓
 * Route to [MetricChannel.open(byte[])]
 *    ↓
 * [Processor]
 *    ↓
 * [Event]
 *    ↓
 * [Validator]
 *    ↓
 * [Repository]
 * </pre>
 *
 * <h4>Error Handling</h4>
 * <ul>
 *     <li>Throws {@code IllegalStateException} if no handlers are registered.</li>
 *     <li>Throws {@code IllegalArgumentException} if no handler matches the header byte.</li>
 * </ul>
 *
 * @see MetricChannel
 * @see MetricProcessor
 * @see MetricValidator
 *
 * @author yungwang-o
 * @version 1.0.0
 */
@Collector
@RequiredArgsConstructor
public class MetricCollector {

    private final Set<MetricChannel> handlers;

    public void collect(final ByteBuffer byteBuffer) {
        if(handlers.isEmpty()) {
            throw new IllegalStateException("collector is empty..");
        }

        byte[] data = convert(byteBuffer);

        byte header = data[0];

        MetricChannel metricChannel = handlers.stream()
                .filter(handler -> handler.getHeaderSpec().equals(header))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No Support Handler.."));

        metricChannel.open(data);
    }

    private byte[] convert(final ByteBuffer byteBuffer) {
        byte[] data = new byte[byteBuffer.remaining()];
        byteBuffer.get(data);

        return data;
    }
}
