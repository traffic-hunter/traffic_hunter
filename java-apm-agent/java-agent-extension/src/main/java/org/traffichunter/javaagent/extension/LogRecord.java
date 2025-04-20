/*
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
package org.traffichunter.javaagent.extension;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.logs.Severity;
import io.opentelemetry.sdk.common.InstrumentationScopeInfo;
import io.opentelemetry.sdk.resources.Resource;
import java.util.Map;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
public class LogRecord {

    private final Map<String, String> resource;

    private final Map<String, String> instrumentationScopeInfo;

    private final Map<String, String> attributes;

    private final String body;

    private final int totalAttributeCount;

    private final Severity severity;

    private final String severityText;

    private final long observedTimestampEpochNanos;

    private final long timestampEpochNanos;

    private LogRecord(Builder builder) {
        this.resource = builder.resource;
        this.instrumentationScopeInfo = builder.instrumentationScopeInfo;
        this.attributes = builder.attributes;
        this.body = builder.body;
        this.totalAttributeCount = builder.totalAttributeCount;
        this.severity = builder.severity;
        this.severityText = builder.severityText;
        this.observedTimestampEpochNanos = builder.observedTimestampEpochNanos;
        this.timestampEpochNanos = builder.timestampEpochNanos;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Map<String, String> resource;

        private Map<String, String> instrumentationScopeInfo;

        private Map<String, String> attributes;

        private String body;

        private int totalAttributeCount;

        private Severity severity;

        private String severityText;

        private long observedTimestampEpochNanos;

        private long timestampEpochNanos;

        public Builder resource(Resource resource) {
            this.resource = OpenTelemetryParser.doParse(resource);
            return this;
        }

        public Builder instrumentationScopeInfo(InstrumentationScopeInfo info) {
            this.instrumentationScopeInfo = OpenTelemetryParser.doParse(info);
            return this;
        }

        public Builder attributes(Attributes attributes) {
            this.attributes = OpenTelemetryParser.doParse(attributes);
            return this;
        }

        public Builder body(String body) {
            this.body = body;
            return this;
        }

        public Builder totalAttributeCount(int count) {
            this.totalAttributeCount = count;
            return this;
        }

        public Builder severity(Severity severity) {
            this.severity = severity;
            return this;
        }

        public Builder severityText(String text) {
            this.severityText = text;
            return this;
        }

        public Builder observedTimestampEpochNanos(long ts) {
            this.observedTimestampEpochNanos = ts;
            return this;
        }

        public LogRecord.Builder timestampEpochNanos(long ts) {
            this.timestampEpochNanos = ts;
            return this;
        }

        public LogRecord build() {
            return new LogRecord(this);
        }
    }

    public Map<String, String> getResource() { return resource; }

    public Map<String, String> getInstrumentationScopeInfo() { return instrumentationScopeInfo; }

    public Map<String, String> getAttributes() { return attributes; }

    public String getBody() { return body; }

    public int getTotalAttributeCount() { return totalAttributeCount; }

    public Severity getSeverity() { return severity; }

    public String getSeverityText() { return severityText; }

    public long getObservedTimestampEpochNanos() { return observedTimestampEpochNanos; }

    public long getTimestampEpochNanos() { return timestampEpochNanos; }
}
