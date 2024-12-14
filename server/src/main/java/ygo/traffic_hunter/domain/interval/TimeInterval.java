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
package ygo.traffic_hunter.domain.interval;

import lombok.Getter;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
@Getter
public enum TimeInterval {

    REAL_TIME("3 seconds", 5_000),
    FIVE_MINUTES("5 minutes", 5_000),
    TEN_MINUTES("10 minutes", 5_000),
    THIRTY_MINUTES("30 minutes", 8_000),
    ONE_HOUR("1 hours", 10_000),
    THREE_HOURS("3 hours", 10_000),
    SIX_HOURS("6 hours", 30_000),
    TWELVE_HOURS("12 hours", 30_000),
    ONE_DAYS("1 days", 60_000),
    TWO_DAYS("2 days", 60_000),
    THREE_DAYS("3 days", 60_000),
    ;

    private final String interval;
    private final long delayMillis;

    TimeInterval(final String interval, final long delayMillis) {
        this.interval = interval;
        this.delayMillis = delayMillis;
    }
}
