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
package org.traffichunter.javaagent.bootstrap.engine.env;

import java.io.InputStream;
import org.traffichunter.javaagent.bootstrap.engine.env.yaml.YamlConfigurableEnvironment;
import org.traffichunter.javaagent.bootstrap.engine.property.TrafficHunterAgentProperty;

/**
 * The {@code ConfigurableEnvironment} interface defines the contract for loading
 * agent configuration properties. It supports loading configuration from
 * default or provided input sources.
 *
 * <p>Features:</p>
 * <ul>
 *     <li>Loads configuration properties into a {@link TrafficHunterAgentProperty} instance.</li>
 *     <li>Supports default and custom input streams for configuration sources.</li>
 * </ul>
 *
 * @see TrafficHunterAgentProperty
 * @see YamlConfigurableEnvironment
 *
 * @author yungwang-o
 * @version 1.0.0
 */
public interface ConfigurableEnvironment {

    TrafficHunterAgentProperty load();

    TrafficHunterAgentProperty load(InputStream is);
}
