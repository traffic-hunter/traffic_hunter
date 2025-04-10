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
package ygo.traffic_hunter.core.schedule;

import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
public class ThreadPoolTaskSchedulerFactory {

    private static final String THREAD_NAME_PREFIX = "TaskScheduler";

    private static final int DEFAULT_TERMINATION_SECONDS = 60;

    private static final boolean DEFAULT_WAIT_FOR_TASKS_COMPLETION = true;

    public static ThreadPoolTaskScheduler create() {

        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();

        scheduler.setThreadNamePrefix(THREAD_NAME_PREFIX);
        scheduler.setWaitForTasksToCompleteOnShutdown(DEFAULT_WAIT_FOR_TASKS_COMPLETION);
        scheduler.setAwaitTerminationSeconds(DEFAULT_TERMINATION_SECONDS);
        scheduler.initialize();

        return scheduler;
    }

    public static ThreadPoolTaskScheduler create(final int poolSize) {

        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();

        scheduler.setPoolSize(poolSize);
        scheduler.setThreadNamePrefix(THREAD_NAME_PREFIX);
        scheduler.setWaitForTasksToCompleteOnShutdown(DEFAULT_WAIT_FOR_TASKS_COMPLETION);
        scheduler.setAwaitTerminationSeconds(DEFAULT_TERMINATION_SECONDS);
        scheduler.initialize();

        return scheduler;
    }
}
