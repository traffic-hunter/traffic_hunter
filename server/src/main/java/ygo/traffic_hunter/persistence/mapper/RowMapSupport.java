/**
 * The MIT License
 *
 * Copyright (c) 2024 traffic-hunter.org
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
package ygo.traffic_hunter.persistence.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class RowMapSupport<T> {

    private final ObjectMapper objectMapper;

    protected final String serialize0(final T object) {

        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize object to JSON = {}", e.getMessage());
        }

        return "";
    }

    protected final String serialize0(final List<T> object) {

        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize object to JSON = {}", e.getMessage());
        }

        return "";
    }

    protected final <C> C deserialize(final String json, final Class<C> clazz) {

        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize JSON = {}", e.getMessage());
        }

        return null;
    }

    protected final <C> List<C> deserializeList(final String json, final Class<C> clazz) {

        try {

            if(json == null || json.isEmpty()) {
                log.warn("json is null or empty");
                return Collections.emptyList();
            }

            JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, clazz);

            return objectMapper.readValue(json, javaType);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize JSON = {}", e.getMessage());
        }

        return Collections.emptyList();
    }
}
