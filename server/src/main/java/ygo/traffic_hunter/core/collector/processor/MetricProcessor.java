package ygo.traffic_hunter.core.collector.processor;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import ygo.traffic_hunter.core.annotation.Processor;
import ygo.traffic_hunter.core.collector.processor.compress.ByteArrayMetricDecompressor;
import ygo.traffic_hunter.core.dto.request.metadata.MetadataWrapper;
import ygo.traffic_hunter.core.dto.request.systeminfo.SystemInfo;
import ygo.traffic_hunter.core.dto.request.transaction.TransactionInfo;

/**
 * <p>
 *  The {@code MetricProcessor} class is responsible for processing raw metric payloads
 *  into structured data objects. It uses decompression and deserialization to convert
 *  byte arrays into {@code MetadataWrapper} objects.
 * </p>
 *
 * @author yungwang-o
 * @version 1.0.0
 */
@Processor
@RequiredArgsConstructor
public class MetricProcessor {

    private final ByteArrayMetricDecompressor decompressor;

    private final ObjectMapper objectMapper;

    public MetadataWrapper<SystemInfo> processSystemInfo(final byte[] data) {

        byte[] unzipped = decompressor.unzip(data);

        JavaType javaType = objectMapper.getTypeFactory()
                .constructParametricType(MetadataWrapper.class, SystemInfo.class);

        try {
            return objectMapper.readValue(unzipped, javaType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public MetadataWrapper<TransactionInfo> processTransactionInfo(final byte[] data) {

        byte[] unzipped = decompressor.unzip(data);

        JavaType javaType = objectMapper.getTypeFactory()
                .constructParametricType(MetadataWrapper.class, TransactionInfo.class);

        try {
            return objectMapper.readValue(unzipped, javaType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
