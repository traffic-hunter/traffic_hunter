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
package org.traffichunter.javaagent.event.object;

import java.time.Instant;
import org.traffichunter.javaagent.commons.status.AgentStatus;
import org.traffichunter.javaagent.event.AgentEvent;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public class AgentStateEvent extends AgentEvent {

    private final AgentStatus beforeStatus;

    private final AgentStatus afterStatus;

    public AgentStateEvent(final Object source,
                           final AgentStatus beforeStatus,
                           final AgentStatus afterStatus) {

        super(source);
        this.beforeStatus = beforeStatus;
        this.afterStatus = afterStatus;
    }

    public AgentStateEvent(final Object source,
                           final Instant instant,
                           final AgentStatus beforeStatus,
                           final AgentStatus afterStatus) {

        super(source, instant);
        this.beforeStatus = beforeStatus;
        this.afterStatus = afterStatus;
    }

    public AgentStatus getBeforeStatus() {
        return this.beforeStatus;
    }

    public AgentStatus getAfterStatus() {
        return this.afterStatus;
    }
}
