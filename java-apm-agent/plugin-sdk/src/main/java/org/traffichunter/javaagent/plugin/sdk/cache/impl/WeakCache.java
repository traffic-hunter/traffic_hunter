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
package org.traffichunter.javaagent.plugin.sdk.cache.impl;

import java.util.WeakHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import org.traffichunter.javaagent.plugin.sdk.cache.Cache;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
public class WeakCache<K, V> implements Cache<K, V> {

    private final WeakHashMap<K, V> map = new WeakHashMap<>();

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    @Override
    public V computeIfAbsent(final K key, final Function<? super K, ? extends V> mappingFunction) {

        lock.writeLock().lock();

        try {
            return map.computeIfAbsent(key, mappingFunction);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public boolean containsKey(final K key) {

        lock.readLock().lock();

        try {
            return map.containsKey(key);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void put(final K key, final V value) {

        lock.writeLock().lock();

        try {
            map.put(key, value);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public V get(final K key) {

        lock.readLock().lock();

        try {
            return map.get(key);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void remove(final K key) {

        lock.writeLock().lock();

        try {
            map.remove(key);
        } finally {
            lock.writeLock().unlock();
        }
    }
}
