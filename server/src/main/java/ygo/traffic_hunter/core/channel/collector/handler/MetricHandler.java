package ygo.traffic_hunter.core.channel.collector.handler;

public interface MetricHandler {

    byte getHeader();

    void handle(byte[] payload);
}
