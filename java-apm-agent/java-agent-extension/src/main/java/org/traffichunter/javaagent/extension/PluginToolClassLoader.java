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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
public class PluginToolClassLoader extends URLClassLoader {

    private static final String JAVA_PREFIX_JAR_ENTRY = "extension/";

    static {
        ClassLoader.registerAsParallelCapable();
    }

    private PluginToolClassLoader(final URL url, final ClassLoader parent) {
        super(new URL[]{url}, parent);
    }

    private static URLClassLoader create(final ClassLoader parent, final URL url) {
        return new PluginToolClassLoader(url, parent);
    }

    public static ClassLoader generateClassLoader(final ClassLoader parent, final File agentFIle) {

        if(parent == null) {
            throw new IllegalStateException("targetClassLoader is not bootCL");
        }

        List<URL> urlClassLoaders = new ArrayList<>();


        return null;
    }

    private static List<String> parseEntryUrl(final File agnetfile) throws IOException {

        try (JarFile jarFile = new JarFile(agnetfile)){

            return jarFile.stream()
                    .map(ZipEntry::getName)
                    .filter(name -> extractClass(replace(name)))
                    .toList();
        }
    }

    private static String replace(final String jarEntryName) {
        return jarEntryName.replace('.', '/') + ".class";
    }

    private static boolean extractClass(final String getName) {

        return (getName.contains("org/traffichunter/javaagent/plugin") ||
                getName.contains(JAVA_PREFIX_JAR_ENTRY + "otel")) &&
                getName.startsWith(JAVA_PREFIX_JAR_ENTRY) &&
                getName.endsWith(".class");
    }

    @Override
    public URL getResource(final String name) {
        return super.getResource(name);
    }
}
