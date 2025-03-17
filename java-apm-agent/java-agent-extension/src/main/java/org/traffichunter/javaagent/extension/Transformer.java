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

import java.util.List;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.AgentBuilder.Identified.Extendable;
import net.bytebuddy.agent.builder.AgentBuilder.Transformer.ForAdvice;
import net.bytebuddy.asm.TypeConstantAdjustment;
import org.traffichunter.javaagent.extension.AbstractPluginInstrumentation.Advice;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
public final class Transformer {

    private AgentBuilder.Identified.Extendable agentBuilder;

    public Transformer(final Extendable agentBuilder) {
        this.agentBuilder = agentBuilder;
    }

    public void processAdvice(final List<Advice> advices) {

        advices.forEach(advice -> {

            ForAdvice forAdvice = new ForAdvice()
                    .include(
                            Utilizr.getBootstrapClassLoader(),
                            Utilizr.getSystemClassLoader(),
                            Utilizr.getAgentClassLoader()
                    )
                    .advice(advice.methodMatcher(), advice.adviceName());

            agentBuilder = agentBuilder.transform(forAdvice);
        });

    }

    public static AgentBuilder.Transformer defaultTransform() {
        return (builder, typeDescription, classLoader, javaModule, protectionDomain) ->
                builder.visit(TypeConstantAdjustment.INSTANCE);
    }
}
