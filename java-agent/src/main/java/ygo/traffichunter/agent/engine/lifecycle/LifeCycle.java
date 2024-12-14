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
package ygo.traffichunter.agent.engine.lifecycle;

import java.time.Duration;
import java.time.Instant;

/**
 * The {@code LifeCycle} abstract class defines a common structure for managing
 * the lifecycle of an object, including its start time, end time, and uptime.
 *
 * <p>Features:</p>
 * <ul>
 *     <li>Automatically initializes the start time when an instance is created.</li>
 *     <li>Provides abstract methods for retrieving the start time, end time, and uptime.</li>
 *     <li>Allows subclasses to implement specific behaviors for managing lifecycle information.</li>
 * </ul>
 *
 * <p>Usage:</p>
 * <pre>{@code
 * public class TaskLifeCycle extends LifeCycle {
 *
 *     @Override
 *     public Instant getStartTime() {
 *         return startTime;
 *     }
 *
 *     @Override
 *     public Instant getEndTime() {
 *         if (endTime == null) {
 *             endTime = Instant.now();
 *         }
 *         return endTime;
 *     }
 *
 *     @Override
 *     public Duration getUpTime() {
 *         return Duration.between(getStartTime(), getEndTime());
 *     }
 * }
 *
 * TaskLifeCycle task = new TaskLifeCycle();
 * System.out.println("Uptime: " + task.getUpTime().toSeconds() + " seconds");
 * }</pre>
 *
 * @see Instant
 * @see Duration
 *
 * @author yungwang-o
 * @version 1.0.0
*/
public abstract class LifeCycle {

    protected final Instant startTime;

    protected Instant endTime;

    protected LifeCycle() {
        this.startTime = Instant.now();
    }

    public abstract Instant getStartTime();

    public abstract Instant getEndTime();

    public abstract Double getUpTime();
}
