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
package ygo.traffic_hunter.domain.entity.alarm;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ygo.traffic_hunter.core.dto.request.systeminfo.cpu.CpuStatusInfo;
import ygo.traffic_hunter.core.dto.request.systeminfo.dbcp.HikariDbcpInfo;
import ygo.traffic_hunter.core.dto.request.systeminfo.memory.MemoryStatusInfo;
import ygo.traffic_hunter.core.dto.request.systeminfo.thread.ThreadStatusInfo;
import ygo.traffic_hunter.core.dto.request.systeminfo.web.tomcat.TomcatWebServerInfo;
import ygo.traffic_hunter.core.dto.response.alarm.ThresholdResponse;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Threshold {

    private static final int DEFAULT_SETTING_PERCENTAGE = 80;

    public static final Threshold DEFAULT = Threshold.builder()
            .cpuThreshold(DEFAULT_SETTING_PERCENTAGE)
            .memoryThreshold(DEFAULT_SETTING_PERCENTAGE)
            .threadThreshold(DEFAULT_SETTING_PERCENTAGE)
            .webRequestThreshold(100)
            .webThreadThreshold(DEFAULT_SETTING_PERCENTAGE)
            .dbcpThreshold(DEFAULT_SETTING_PERCENTAGE)
            .build();

    private Integer id;

    private int cpuThreshold;

    private int memoryThreshold;

    private int threadThreshold;

    private int webRequestThreshold;

    private int webThreadThreshold;

    private int dbcpThreshold;

    @Builder
    public Threshold(final int cpuThreshold,
                     final int memoryThreshold,
                     final int threadThreshold,
                     final int webRequestThreshold,
                     final int webThreadThreshold,
                     final int dbcpThreshold) {

        this.cpuThreshold = cpuThreshold;
        this.memoryThreshold = memoryThreshold;
        this.threadThreshold = threadThreshold;
        this.webRequestThreshold = webRequestThreshold;
        this.webThreadThreshold = webThreadThreshold;
        this.dbcpThreshold = dbcpThreshold;
    }

    // early return
    public boolean isInvalid(final Threshold threshold) {

        if (threshold == null) {
            return true;
        }

        int[] values = {
                threshold.cpuThreshold,
                threshold.memoryThreshold,
                threshold.webThreadThreshold,
                threshold.dbcpThreshold
        };

        for (int value : values) {
            if (value < 0 || value > 100) {
                return true;
            }
        }

        return false;
    }

    @Builder
    public record Calculator(

            MemoryStatusInfo memoryStatusInfo,

            CpuStatusInfo cpuStatusInfo,

            ThreadStatusInfo threadStatusInfo,

            TomcatWebServerInfo tomcatWebServerInfo,

            HikariDbcpInfo hikariDbcpInfo
    ) {

        public CalculatedThreshold calculate(final ThresholdResponse threshold) {

            return CalculatedThreshold.builder()
                    .calculateCpu(threshold.cpuThreshold())
                    .calculateMemory((long) (memoryStatusInfo.heapMemoryUsage().max() * (threshold.memoryThreshold()
                            / 100.0)))
                    .calculateThread(
                            (long) (threadStatusInfo.getTotalStartThreadCount() * (threshold.threadThreshold()
                                    / 100.0)))
                    .calculateWebRequest(threshold.webRequestThreshold())
                    .calculateWebThread((int) (tomcatWebServerInfo.tomcatThreadPoolInfo().maxThreads() * (
                            threshold.webRequestThreshold() / 100.0)))
                    .calculateDbcp((int) (hikariDbcpInfo.totalConnections() * (threshold.dbcpThreshold() / 100.0)))
                    .build();
        }
    }

    @Builder
    public record CalculatedThreshold(

            int calculateCpu,

            long calculateMemory,

            long calculateThread,

            long calculateWebRequest,

            int calculateWebThread,

            int calculateDbcp
    ) {
    }
}
