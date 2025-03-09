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
package org.traffichunter.javaagent.extension.bootstrap.env.yaml;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;
import org.traffichunter.javaagent.commons.util.FileUtils;
import org.traffichunter.javaagent.extension.bootstrap.env.ConfigurableEnvironment;
import org.traffichunter.javaagent.extension.bootstrap.env.yaml.bind.RelaxedBindingUtils;
import org.traffichunter.javaagent.extension.bootstrap.env.yaml.root.RootYamlProperty;
import org.traffichunter.javaagent.extension.bootstrap.env.yaml.root.agent.retry.backoff.BackOffSubProperty;
import org.traffichunter.javaagent.extension.bootstrap.property.TrafficHunterAgent;
import org.traffichunter.javaagent.extension.bootstrap.property.TrafficHunterAgentProperty;
import org.traffichunter.javaagent.retry.backoff.BackOffPolicy;
import org.traffichunter.javaagent.retry.backoff.policy.ExponentialBackOffPolicy;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

/**
 * The {@code YamlConfigurableEnvironment} class implements {@link ConfigurableEnvironment}
 * to load configuration properties from a YAML file or input stream.
 *
 * <p>Features:</p>
 * <ul>
 *     <li>Loads configuration properties from a YAML file or input stream.</li>
 *     <li>Parses YAML data into a {@link TrafficHunterAgentProperty} instance using
 *         custom relaxed binding rules.</li>
 *     <li>Supports a default configuration file or custom file paths.</li>
 * </ul>
 *
 * @see ConfigurableEnvironment
 * @see TrafficHunterAgentProperty
 *
 * @author yungwang-o
 * @version 1.0.0
 */
public class YamlConfigurableEnvironment implements ConfigurableEnvironment {

    private static final String DEFAULT_CONFIG_FILE = "agent-env.yml";

    private final Yaml yaml;

    private final String path;

    public YamlConfigurableEnvironment(final String path) {
        final Constructor constructor = new Constructor(RootYamlProperty.class, new LoaderOptions());

        constructor.setPropertyUtils(new RelaxedBindingUtils());

        this.yaml = new Yaml(constructor);
        this.path = path;
    }

    @Override
    public TrafficHunterAgentProperty load() {
        final InputStream is = FileUtils.getFile(path);

        final RootYamlProperty root = yaml.load(is);

        return YamlParser.parse(root);
    }

    @Override
    public TrafficHunterAgentProperty load(final InputStream is) {

        final RootYamlProperty root = yaml.load(is);

        return YamlParser.parse(root);
    }

    static class YamlParser {

        private static TrafficHunterAgentProperty parse(final RootYamlProperty root){

            return TrafficHunterAgent.connect(root.getAgent().getServerUri())
                    .name(root.getAgent().getName())
                    .targetUri(root.getAgent().getTargetUri())
                    .locationJar(root.getAgent().getJar())
                    .scheduleInterval(root.getAgent().getInterval())
                    .scheduleTimeUnit(TimeUnit.SECONDS)
                    .faultTolerant()
                    .retry(root.getAgent().getRetry().getMaxAttempt())
                    .backOffPolicy(backOffPolicy(root.getAgent().getRetry().getBackoff()))
                    .complete();
        }

        private static BackOffPolicy backOffPolicy(BackOffSubProperty backOffSubProperty) {
            if(backOffSubProperty == null) {
                return ExponentialBackOffPolicy.DEFAULT;
            }

            return new ExponentialBackOffPolicy(
                    backOffSubProperty.getIntervalMillis(),
                    backOffSubProperty.getMultiplier()
            );
        }
    }
}
