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
package org.traffichunter.javaagent.extension;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.AgentBuilder.Identified.Extendable;
import net.bytebuddy.agent.builder.AgentBuilder.Transformer.ForAdvice;
import net.bytebuddy.dynamic.ClassFileLocator.ForClassLoader;
import org.traffichunter.javaagent.extension.AbstractPluginInstrumentation.Advices;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
public final class Transformer {

    private AgentBuilder.Identified.Extendable agentBuilder;

    public Transformer(final Extendable agentBuilder) {
        this.agentBuilder = agentBuilder;
    }

    public void processAdvice(final Advices advices) {

        ForAdvice forAdvice = new ForAdvice()
                .include(
                        ForClassLoader.ofBootLoader(),
                        ForClassLoader.ofPlatformLoader(),
                        ForClassLoader.ofSystemLoader()
                ).advice(advices.methodMatcher(), advices.adviceClass().getName());

        agentBuilder = agentBuilder.transform(forAdvice);
    }

    AgentBuilder.Identified.Extendable agentBuilder() {
        return agentBuilder;
    }
}
