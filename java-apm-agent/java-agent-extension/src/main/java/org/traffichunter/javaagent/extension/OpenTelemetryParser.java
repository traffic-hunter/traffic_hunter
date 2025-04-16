package org.traffichunter.javaagent.extension;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.sdk.common.InstrumentationScopeInfo;
import io.opentelemetry.sdk.resources.Resource;
import java.util.HashMap;
import java.util.Map;

final class OpenTelemetryParser {

    public static Map<String, String> doParse(final Attributes attributes) {
        Map<String, String> attributesMap = new HashMap<>();

        Map<AttributeKey<?>, Object> asMap = attributes.asMap();

        for (Map.Entry<AttributeKey<?>, Object> entry : asMap.entrySet()) {
            attributesMap.put(entry.getKey().toString(), entry.getValue().toString());
        }

        return attributesMap;
    }

    public static Map<String, String> doParse(final Resource resource) {
        Map<String, String> resourceMap = new HashMap<>();

        Map<AttributeKey<?>, Object> asMap = resource.getAttributes().asMap();

        for (Map.Entry<AttributeKey<?>, Object> entry : asMap.entrySet()) {
            resourceMap.put(entry.getKey().toString(), entry.getValue().toString());
        }

        return resourceMap;
    }

    public static Map<String, String> doParse(final InstrumentationScopeInfo instrumentationScopeInfo) {
        Map<String, String> instrumentationScopeInfoMap = new HashMap<>();

        Map<AttributeKey<?>, Object> asMap = instrumentationScopeInfo.getAttributes().asMap();

        for (Map.Entry<AttributeKey<?>, Object> entry : asMap.entrySet()) {
            instrumentationScopeInfoMap.put(entry.getKey().toString(), entry.getValue().toString());
        }

        return instrumentationScopeInfoMap;
    }
}
