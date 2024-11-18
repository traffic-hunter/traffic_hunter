package ygo.traffic_hunter.core.converter.transaction;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ygo.traffic_hunter.core.converter.ByteArrayMetricConverter;
import ygo.traffic_hunter.dto.systeminfo.TransactionInfo;
import ygo.traffic_hunter.dto.systeminfo.metadata.MetadataWrapper;

@Component
@RequiredArgsConstructor
public class TransactionMetricConverter extends ByteArrayMetricConverter<TransactionInfo> {

    private final ObjectMapper objectMapper;

    @Override
    public MetadataWrapper<TransactionInfo> convert(final byte[] data) {
        byte[] unzippedData = unzip(data);

        try {
            return objectMapper.readValue(unzippedData, new TypeReference<>() {});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
