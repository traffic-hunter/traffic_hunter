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
package org.traffichunter.javaagent.extension.bootstrap.property;

import java.util.concurrent.TimeUnit;
import org.traffichunter.javaagent.retry.backoff.BackOffPolicy;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public record TrafficHunterAgentProperty(
        String name,
        String targetUri,
        String jar,
        int scheduleInterval,
        String serverUri,
        TimeUnit timeUnit,
        int maxAttempt,
        BackOffPolicy backOffPolicy
) {

    public TrafficHunterAgentProperty(final String name,
                                      final String targetUri,
                                      final String jar,
                                      final int scheduleInterval,
                                      final String serverUri,
                                      final TimeUnit timeUnit,
                                      final int maxAttempt,
                                      final BackOffPolicy backOffPolicy) {

        this.name = name;
        this.targetUri = targetUri;
        this.jar = jar;
        this.scheduleInterval = scheduleInterval;
        this.serverUri = serverUri;
        this.timeUnit = timeUnit;
        this.maxAttempt = maxAttempt;
        this.backOffPolicy = backOffPolicy;
    }

    public TrafficHunterAgentProperty(final String name,
                                      final String targetUri,
                                      final String jar,
                                      final int scheduleInterval,
                                      final String serverUri,
                                      final TimeUnit timeUnit) {

        this(name, targetUri, jar, scheduleInterval, serverUri, timeUnit, 0, null);
    }

    @Override
    public String toString() {
        return "TrafficHunterAgentProperty{" +
                "name='" + name + '\'' +
                ", targetUri='" + targetUri + '\'' +
                ", jar=" + jar +
                ", scheduleInterval=" + scheduleInterval +
                ", serverUri=" + serverUri +
                ", timeUnit=" + timeUnit +
                ", maxAttempt=" + maxAttempt +
                ", backOffPolicy=" + backOffPolicy.toString() +
                '}';
    }
}
