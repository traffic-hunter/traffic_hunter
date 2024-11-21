package ygo.traffic_hunter.core.channel.collector.processor.compress;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import org.springframework.stereotype.Component;

@Component
public class ByteArrayMetricDecompressor {

    public byte[] unzip(final byte[] data) {

        byte[] copy = new byte[data.length - 1];

        System.arraycopy(data, 1, copy, 0, copy.length);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(copy))) {
            byte[] result = new byte[2048];

            int len;
            while ((len = gzipInputStream.read(result)) != -1) {
                baos.write(result, 0, len);
            }

            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
