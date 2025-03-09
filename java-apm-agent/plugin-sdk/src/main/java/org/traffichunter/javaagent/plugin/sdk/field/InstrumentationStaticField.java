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
package org.traffichunter.javaagent.plugin.sdk.field;

import java.util.logging.Logger;
import org.traffichunter.javaagent.plugin.sdk.field.map.FieldMap;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
public class InstrumentationStaticField {

    private static final Logger log = Logger.getLogger(InstrumentationStaticField.class.getName());

    private InstrumentationStaticField() {}

    @FunctionalInterface
    public interface FieldSupplier {

        <K extends T, V extends F, T, F> PluginSupportField<K, V> find(Class<T> type, Class<F> fieldType);
    }

    private static final FieldSupplier DEFAULT = new MapBasedFieldSupplier();

    private static volatile FieldSupplier INSTANCE = DEFAULT;

    public static FieldSupplier get() {
        return DEFAULT;
    }

    public static void set(final FieldSupplier supplier) {
        if(DEFAULT != INSTANCE) {
            log.warning("FieldSupplier is already set");
        }

        INSTANCE = supplier;
    }

    private static final class MapBasedFieldSupplier implements FieldSupplier {

        private final FieldMap<Class<?>, FieldMap<Class<?>, PluginSupportField<?, ?>>>
                map = FieldMap.weak();

        @Override
        @SuppressWarnings("unchecked")
        public <K extends T, V extends F, T, F> PluginSupportField<K, V> find(final Class<T> type,
                                                                              final Class<F> fieldType) {
            return (PluginSupportField<K, V>)
                    map.computeIfAbsent(type, k -> FieldMap.weak())
                            .computeIfAbsent(fieldType, k -> new InstrumentationSupportFlied<>());
        }
    }

    private static class InstrumentationSupportFlied<K, V> implements PluginSupportField<K, V> {

        private final FieldMap<K, V> fieldMap = FieldMap.weak();

        @Override
        public void set(final K key, final V value) {
            fieldMap.put(key, value);
        }

        @Override
        public V get(final K key) {
            return fieldMap.get(key);
        }
    }
}
