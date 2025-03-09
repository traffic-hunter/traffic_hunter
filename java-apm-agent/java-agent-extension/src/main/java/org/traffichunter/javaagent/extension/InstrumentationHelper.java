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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.AgentBuilder.Identified.Extendable;
import net.bytebuddy.agent.builder.AgentBuilder.Transformer.ForAdvice;
import org.traffichunter.javaagent.extension.bytebuddy.Transformer;
import org.traffichunter.javaagent.plugin.instrumentation.AbstractPluginInstrumentation;
import org.traffichunter.javaagent.plugin.instrumentation.AbstractPluginInstrumentation.Advice;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
public class InstrumentationHelper {

    private static final Logger log = Logger.getLogger(InstrumentationHelper.class.getName());

    private final List<AbstractPluginInstrumentation> pluginInstrumentation;

    private final Set<String> pluginNameSet;

    private final List<String> pluginDetailNameList;

    public InstrumentationHelper(final List<AbstractPluginInstrumentation> pluginInstrumentation) {

        this.pluginNameSet = new LinkedHashSet<>();
        this.pluginDetailNameList = new ArrayList<>(pluginInstrumentation.size());

        for(AbstractPluginInstrumentation plugins : pluginInstrumentation) {
            this.pluginNameSet.add(plugins.getPluginName());
            this.pluginDetailNameList.add(plugins.getPluginDetailName());
        }

        this.pluginInstrumentation = pluginInstrumentation;
    }

    public AgentBuilder instrument(final AgentBuilder originalAgentBuilder) {

        if(isEnabled()) {
            log.warning("Instrumenting is empty!");
            return originalAgentBuilder;
        }

        AgentBuilder agentBuilder = originalAgentBuilder;

        InstrumentationInjector instInjector = new InstrumentationInjector(
                mainPluginModuleName(),
                Collections.emptyList(),
                Collections.emptyList()
        );

        for(final AbstractPluginInstrumentation plugin : pluginInstrumentation) {
            AgentBuilder.Identified.Extendable extendableAgentBuilder = agentBuilder
                    .type(plugin.typeMatcher())
                    .transform(instInjector)
                    .transform(Transformer.defaultTransform());

            extendableAgentBuilder = applyTransformer(plugin, extendableAgentBuilder);

            agentBuilder = extendableAgentBuilder;
        }

        return agentBuilder;
    }

    private AgentBuilder.Identified.Extendable applyTransformer(final AbstractPluginInstrumentation plugin,
                                                                Extendable agentBuilder) {

        for(final Advice advice : plugin.transform()) {

            AgentBuilder.Transformer.ForAdvice forAdvice = new ForAdvice()
                    .include(Utilizr.getBootstrapClassLoader(), Utilizr.getAgentClassLoader())
                    .advice(advice.methodMatcher(), advice.adviceName());

            agentBuilder = agentBuilder.transform(forAdvice);
        }

        return agentBuilder;
    }

    private String mainPluginModuleName() {
        return pluginNameSet.iterator().next();
    }

    private boolean isEnabled() {
        return pluginInstrumentation == null || pluginInstrumentation.isEmpty();
    }
}
