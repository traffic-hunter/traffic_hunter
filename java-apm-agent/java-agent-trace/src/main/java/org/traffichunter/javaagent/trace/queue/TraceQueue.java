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
package org.traffichunter.javaagent.trace.queue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.traffichunter.javaagent.trace.dto.TraceInfo;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public enum TraceQueue {

    INSTANCE,
    ;

    private final BlockingQueue<TraceInfo> syncQ = new LinkedBlockingQueue<>(100);

    /**
     * this method is non-blocking
     * @return success : true, fail : false
     */
    public boolean add(final TraceInfo trInfo) {
        return syncQ.offer(trInfo);
    }

    /**
     * this method is blocking
     */
    public TraceInfo poll() throws InterruptedException {
        return syncQ.take();
    }

    public void removeAll() {
        syncQ.clear();
    }

    public int size() {
        return syncQ.size();
    }
}
