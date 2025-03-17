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

import static net.bytebuddy.matcher.ElementMatchers.named;

import java.util.List;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDefinition;
import org.traffichunter.javaagent.bootstrap.TrafficHunterAgentClassLoader;
import org.traffichunter.javaagent.bootstrap.TrafficHunterAgentClassLoader.BootStrapClassLoderProxy;
import org.traffichunter.javaagent.extension.bootstrap.ConfigurableContextInitializer;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
public class Utilizr {

    public static final List<String> BOOTSTRAP_PACKAGE_PREFIXES = List.of(
            "org.traffichunter.javaagent.bootstrap",
            "org.traffichunter.javaagent.plugin.sdk"
    );

    public static BootStrapClassLoderProxy getBootstrapClassLoader() {
        if (getAgentClassLoader() instanceof TrafficHunterAgentClassLoader trafficHunterAgentClassLoader) {
            return new BootStrapClassLoderProxy(trafficHunterAgentClassLoader);
        }

        return new BootStrapClassLoderProxy(null);
    }

    public static ClassLoader getAgentClassLoader() {
        return ConfigurableContextInitializer.class.getClassLoader();
    }

    public static ClassLoader getPlatformClassLoader() {
        return ClassLoader.getPlatformClassLoader();
    }

    public static ClassLoader getSystemClassLoader() {
        return ClassLoader.getSystemClassLoader();
    }

    public static String replaceInternalPath(final Class<?> clazz) {
        return clazz.getName().replace('.', '/');
    }

    public static String replaceClassPath(final String internalPath) {
        return internalPath.replace('/', '.');
    }

    public static String replaceInnerClass(final String className) {
        return className.replace('.', '$');
    }

    public static String replaceInternalPathClass(final Class<?> clazz) {
        return clazz.getName().replace('.', '/') + ".class";
    }

    public static MethodDescription getMethodDefinition(final TypeDefinition type, final String methodName) {
        return type.getDeclaredMethods().filter(named(methodName)).getOnly();
    }
}
