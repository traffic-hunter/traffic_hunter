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
package org.traffichunter.javaagent.extension;

import static net.bytebuddy.matcher.ElementMatchers.any;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatcher.Junction;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
public abstract class AbstractPluginInstrumentation {

    private final String pluginName;

    private final String pluginDetailName;

    private final String pluginModuleVersion;

    public AbstractPluginInstrumentation(final String pluginName,
                                         final String pluginDetailName,
                                         final String pluginModuleVersion) {
        this.pluginName = pluginName;
        this.pluginDetailName = pluginDetailName;
        this.pluginModuleVersion = pluginModuleVersion;
    }

    public abstract void transform(Transformer transformer);

    public abstract ElementMatcher<? super TypeDescription> typeMatcher();

    protected abstract ElementMatcher<? super MethodDescription> isMethod();

    protected Junction<ClassLoader> classLoaderMatcher() { return any(); }

    public Junction<TypeDescription> ignorePackage() {
        return null;
    }

    public String getPluginName() {
        return pluginName;
    }

    public String getPluginDetailName() {
        return pluginDetailName;
    }

    public String getPluginModuleVersion() {
        return pluginModuleVersion;
    }

    public static final class Advices {

        private final ElementMatcher<? super MethodDescription> methodMatcher;

        private final Class<?> adviceClass;

        private Advices(final ElementMatcher<? super MethodDescription> methodMatcher, final Class<?> adviceClass) {
            this.methodMatcher = methodMatcher;
            this.adviceClass = adviceClass;
        }

        public static Advices create(final ElementMatcher<? super MethodDescription> methodMatcher, final Class<?> adviceClass) {
            return new Advices(methodMatcher, adviceClass);
        }

        public static String combineClassBinaryPath(final Class<?> pluginClassName,
                                                    final Class<?> innerPluginAdviceClassName) {

            return pluginClassName.getName() + "$" + innerPluginAdviceClassName.getSimpleName();
        }

        public ElementMatcher<? super MethodDescription> methodMatcher() {
            return methodMatcher;
        }

        public Class<?> adviceClass() {
            return adviceClass;
        }

        @Override
        public String toString() {
            return "Advice{" +
                    "methodMatcher=" + methodMatcher +
                    ", adviceClass=" + adviceClass +
                    '}';
        }
    }
}
