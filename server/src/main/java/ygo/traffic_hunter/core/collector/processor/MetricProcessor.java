/**
 * The MIT License
 *
 * Copyright (c) 2024 yungwang-o
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package ygo.traffic_hunter.core.collector.processor;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import ygo.traffic_hunter.core.annotation.Processor;
import ygo.traffic_hunter.core.collector.processor.compress.ByteArrayMetricDecompressor;
import ygo.traffic_hunter.core.dto.request.metadata.MetadataWrapper;
import ygo.traffic_hunter.core.dto.request.systeminfo.SystemInfo;
import ygo.traffic_hunter.domain.metric.TraceInfo;

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

    public MetadataWrapper<TraceInfo> processTraceInfo(final byte[] data) {

        byte[] unzipped = decompressor.unzip(data);

        JavaType javaType = objectMapper.getTypeFactory()
                .constructParametricType(MetadataWrapper.class, TraceInfo.class);

        try {
            return objectMapper.readValue(unzipped, javaType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
