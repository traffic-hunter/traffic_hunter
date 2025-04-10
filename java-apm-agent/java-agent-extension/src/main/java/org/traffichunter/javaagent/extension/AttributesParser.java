package org.traffichunter.javaagent.extension;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import java.util.HashMap;
import java.util.Map;

final class AttributesParser {

    public static Map<String, String> doParse(final Attributes attributes) {
        Map<String, String> attributesMap = new HashMap<>();

        Map<AttributeKey<?>, Object> asMap = attributes.asMap();

        for (Map.Entry<AttributeKey<?>, Object> entry : asMap.entrySet()) {
            attributesMap.put(entry.getKey().toString(), entry.getValue().toString());
        }

        return attributesMap;
    }
}
