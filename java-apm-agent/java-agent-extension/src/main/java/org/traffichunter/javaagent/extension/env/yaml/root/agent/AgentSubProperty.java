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
package org.traffichunter.javaagent.extension.env.yaml.root.agent;

import org.traffichunter.javaagent.extension.env.yaml.root.agent.retry.RetrySubProperty;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public class AgentSubProperty {

    private String name;
    private String jar;
    private String serverUri;
    private String targetUri;
    private int interval;
    private RetrySubProperty retry;

    public AgentSubProperty() {
    }

    public AgentSubProperty(final String name, final String jar, final String serverUri, final String targetUri,
                            final int interval,
                            final RetrySubProperty retry) {
        this.name = name;
        this.jar = jar;
        this.serverUri = serverUri;
        this.targetUri = targetUri;
        this.interval = interval;
        this.retry = retry;
    }

    public String getServerUri() {
        return serverUri;
    }

    public void setServerUri(final String serverUri) {
        this.serverUri = serverUri;
    }

    public String getTargetUri() {
        return targetUri;
    }

    public void setTargetUri(final String targetUri) {
        this.targetUri = targetUri;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(final int interval) {
        this.interval = interval;
    }

    public RetrySubProperty getRetry() {
        return retry;
    }

    public void setRetry(final RetrySubProperty retry) {
        this.retry = retry;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getJar() {
        return jar;
    }

    public void setJar(final String jar) {
        this.jar = jar;
    }
}
