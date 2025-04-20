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
package ygo.traffic_hunter.core.statistics;

import lombok.Getter;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
@Getter
public enum StatisticsMetricTimeRange {

    LATEST_ONE_HOUR("1 hours"),
    LATEST_TWO_HOURS("2 hours"),
    LATEST_THREE_HOURS("3 hours"),
    LATEST_SIX_HOURS("6 hours"),
    LATEST_TWELVE_HOURS("12 hours"),
    LATEST_ONE_DAY("1 day"),
    LATEST_TWO_DAY("2 day"),
    LATEST_THREE_DAY("3 day"),
    LATEST_ONE_WEEK("1 week"),
    LATEST_ONE_MONTH("1 month"),
    LATEST_TWO_MONTHS("2 month"),
    LATEST_THREE_MONTHS("3 month"),
    LATEST_SIX_MONTHS("6 mont"),
    LATEST_ONE_YEAR("1 year"),
    LATEST_TWO_YEARS("2 year"),
    LATEST_THREE_YEARS("3 year"),
    ;

    private final String latestRange;

    StatisticsMetricTimeRange(final String latestRange) {
        this.latestRange = latestRange;
    }
}
