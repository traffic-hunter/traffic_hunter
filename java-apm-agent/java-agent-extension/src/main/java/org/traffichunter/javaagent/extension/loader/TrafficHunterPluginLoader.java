/**
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
package org.traffichunter.javaagent.extension.loader;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import org.traffichunter.javaagent.extension.AbstractPluginInstrumentation;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
public class TrafficHunterPluginLoader implements PluginLoader<AbstractPluginInstrumentation> {

    @Override
    public List<AbstractPluginInstrumentation> loadModules(final ClassLoader classLoader) {

        List<AbstractPluginInstrumentation> loadPlugIn = new ArrayList<>();

        ServiceLoader<AbstractPluginInstrumentation> loader =
                ServiceLoader.load(AbstractPluginInstrumentation.class, classLoader);

        for(AbstractPluginInstrumentation plugIn : loader) {
            loadPlugIn.add(plugIn);
        }

        return loadPlugIn;
    }
}
