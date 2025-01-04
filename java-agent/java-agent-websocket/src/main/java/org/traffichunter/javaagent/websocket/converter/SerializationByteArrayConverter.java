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
package org.traffichunter.javaagent.websocket.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.traffichunter.javaagent.commons.dto.metadata.MetadataWrapper;

/**
 * The {@code SerializationByteArrayConverter} class provides utility methods for
 * serializing objects into compressed byte arrays and deserializing compressed byte arrays
 * back into objects. This is useful for efficient data transmission or storage.
 *
 * <p>Key Features:</p>
 * <ul>
 *     <li>Serializes objects to compressed byte arrays with a metric type identifier.</li>
 *     <li>Deserializes compressed byte arrays back into objects of specified types.</li>
 *     <li>Supports gzip compression for optimized size reduction.</li>
 * </ul>
 *
 * <p>Usage:</p>
 * <ul>
 *     <li>{@code transform(Object, MetricType)}: Converts an object to a compressed byte array.</li>
 *     <li>{@code inverseTransform(byte[], TypeReference)}: Converts a compressed byte array back into an object.</li>
 * </ul>
 *
 * @see ObjectMapper
 * @see GZIPOutputStream
 * @see GZIPInputStream
 * @author yungwang-o
 * @version 1.0.0
 */
public class SerializationByteArrayConverter {

    private final ObjectMapper objectMapper;

    public SerializationByteArrayConverter(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public byte[] transform(final Object obj, final MetricType metricType) {
        byte[] data = serialize(obj);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()){

            try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(baos)) {
                gzipOutputStream.write(data);
            }

            byte[] compressByteArray = baos.toByteArray();
            byte[] result = new byte[baos.toByteArray().length + 1];

            result[0] = metricType.value;

            System.arraycopy(compressByteArray, 0, result, 1, compressByteArray.length);

            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> MetadataWrapper<T> inverseTransform(final byte[] data,
                                                   final TypeReference<MetadataWrapper<T>> typeReference) {

        byte[] copy = new byte[data.length - 1];

        System.arraycopy(data, 1, copy, 0, copy.length);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(copy))) {
            byte[] result = new byte[2048];

            int len;
            while ((len = gzipInputStream.read(result)) != -1) {
                baos.write(result, 0, len);
            }

            byte[] byteArray = baos.toByteArray();

            return objectMapper.readValue(byteArray, typeReference);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] serialize(final Object object) {
        try {
            return objectMapper.writeValueAsBytes(object);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public enum MetricType {
        SYSTEM_METRIC((byte) 1),
        TRANSACTION_METRIC((byte) 2),
        ;

        private final byte value;

        MetricType(final byte value) {
            this.value = value;
        }
    }
}
