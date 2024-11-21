package ygo.traffic_hunter.core.channel;

import java.io.Closeable;
import java.nio.ByteBuffer;

public interface MetricProcessingChannel extends Closeable {

    void init();

    void process(ByteBuffer buffer);
}
