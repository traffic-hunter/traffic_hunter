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
package ygo.traffichunter.retry.backoff;

import ygo.traffichunter.retry.backoff.policy.ExponentialBackOffPolicy;
import ygo.traffichunter.retry.backoff.policy.FixedBackOffPolicy;

/**
 * The {@code BackOffPolicy} class serves as the base class for defining backoff strategies
 * used in retry mechanisms. It provides common properties such as the interval and multiplier,
 * which can be extended for specific backoff behaviors.
 *
 * <p>Subclasses:</p>
 * <ul>
 *     <li>{@link ExponentialBackOffPolicy}: Implements exponential backoff strategy.</li>
 *     <li>{@link FixedBackOffPolicy}: Implements fixed interval backoff strategy.</li>
 * </ul>
 *
 * @author yungwang-o
 * @version 1.0.0
 */
public class BackOffPolicy {

    private final long intervalMillis;
    private final int multiplier;

    public BackOffPolicy(final long intervalMillis, final int multiplier) {
        this.intervalMillis = intervalMillis;
        this.multiplier = multiplier;
    }

    public long getIntervalMillis() {
        return intervalMillis;
    }

    public int getMultiplier() {
        return multiplier;
    }

    @Override
    public String toString() {
        return "BackOffPolicy{" +
                "intervalMillis=" + intervalMillis +
                ", multiplier=" + multiplier +
                '}';
    }
}
