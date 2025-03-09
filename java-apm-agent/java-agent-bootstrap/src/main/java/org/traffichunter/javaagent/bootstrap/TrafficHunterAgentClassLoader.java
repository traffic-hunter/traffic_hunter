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
package org.traffichunter.javaagent.bootstrap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.security.CodeSource;
import java.security.Permission;
import java.security.cert.Certificate;
import java.util.Enumeration;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * This custom class loader quoted the opentelemetry agent class loader.
 * @author yungwang-o
 * @version 1.1.0
 */
public class TrafficHunterAgentClassLoader extends URLClassLoader {

    private static final String JAVA_EXTENSION_JAR_URL = "javaagent-extension.jar";
    private static final String JAVA_PREFIX_ENTRY = "extension/";

    private static final int MINIMUM_JAVA_VERSION = 17;

    // inst.appendBootstrapClassLoader
    private final BootStrapClassLoderProxy bootstrapClassLoader;

    private final JarFile agentJarFile;
    private final URL agentJarUrl;
    private final CodeSource codeSource;
    private final Manifest manifest;

    static {

        if (!ClassLoader.registerAsParallelCapable()) {
            System.err.println("TrafficHunterAgentClassLoader not registered as parallel");
        }
    }

    public TrafficHunterAgentClassLoader(final File javaagentFile) {
        super(new URL[] {}, ClassLoader.getPlatformClassLoader());

        Objects.requireNonNull(javaagentFile, "javaagentFile");

        try {

            this.bootstrapClassLoader = new BootStrapClassLoderProxy(this);

            this.agentJarFile = new JarFile(javaagentFile, false);
            this.agentJarUrl = new URL("file", null, 0, "/", SpecificClassLoaderURLStreamHandler.getStreamHandler(agentJarFile));
            this.codeSource = new CodeSource(javaagentFile.toURI().toURL(), (Certificate[]) null);
            this.manifest = agentJarFile.getManifest();
        } catch (IOException e) {
            throw new IllegalStateException("Agent file could not be loaded", e);
        }
    }

    @Override
    protected Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {

        synchronized (getClassLoadingLock(name)) {
            Class<?> clazz = findLoadedClass(name);

            if (clazz == null) {
                clazz = findAgentClass(name);
            }

            if (clazz == null) {
                clazz = super.loadClass(name, false);
            }
            if (resolve) {
                resolveClass(clazz);
            }

            return clazz;
        }
    }

    //
    private Class<?> findAgentClass(final String name) throws ClassNotFoundException {
        JarEntry jarEntry = findAgentJarEntry(name.replace('.', '/') + ".class");

        if(jarEntry == null) {
            return null;
        }

        try (InputStream is = agentJarFile.getInputStream(jarEntry)) {
            int size = (int) jarEntry.getSize();
            int offset = 0;
            int read;

            byte[] clazzBuf = new byte[size];

            while (offset < size && (read = is.read(clazzBuf, offset, size - offset))  != -1) {
                offset += read;
            }

            checkDefinedPackage(name);
            return defineClass(name, clazzBuf, 0, clazzBuf.length, codeSource);
        } catch (IOException e) {
            throw new ClassNotFoundException("agent class not found : " + name, e);
        }
    }

    private JarEntry findAgentJarEntry(final String name) {

        String prefixExtensionJarEntry = JAVA_PREFIX_ENTRY + name;

        return agentJarFile.getJarEntry(prefixExtensionJarEntry);
    }

    private void checkDefinedPackage(final String clazz) {
        String packageName = getPackageName(clazz);

        if(packageName == null) {
            return;
        }

        if (getDefinedPackage(packageName) == null) {
            try {
                definePackage(packageName, manifest, codeSource.getLocation());
            } catch (IllegalArgumentException e) {
                if(getDefinedPackage(packageName) == null) {
                    throw new IllegalStateException("Package not define", e);
                }
            }
        }
    }

    @Override
    public URL getResource(final String name) {
        URL url = bootstrapClassLoader.getResource(name);

        if(url != null) {
            return url;
        }

        return super.getResource(name);
    }

    @Override
    public URL findResource(final String name) {
        JarEntry jarEntry = findAgentJarEntry(name);

        URL entryUrl = getJarEntryUrl(jarEntry);
        if (entryUrl != null) {
            return entryUrl;
        }

        return super.findResource(name);
    }

    @Override
    public Enumeration<URL> findResources(final String name) throws IOException {
        Enumeration<URL> resources = super.findResources(name);

        JarEntry agentJarEntry = findAgentJarEntry(name);

        URL entryUrl = getJarEntryUrl(agentJarEntry);
        if (entryUrl != null) {
            return new Enumeration<>() {
                boolean next = true;

                @Override
                public boolean hasMoreElements() {
                    return next || resources.hasMoreElements();
                }

                @Override
                public URL nextElement() {
                    if(next) {
                        next = false;
                        return entryUrl;
                    }

                    return resources.nextElement();
                }
            };
        }

        return resources;
    }

    private URL getJarEntryUrl(final JarEntry jarEntry) {
        if(jarEntry == null) {
            return null;
        }

        try {
            return new URL(agentJarUrl, jarEntry.getName());
        } catch (MalformedURLException e) {
            throw new IllegalStateException("Failed to generate jar entry url", e);
        }
    }

    private static String getPackageName(final String clazz) {
        int idx = clazz.lastIndexOf(".");

        return idx == -1 ? null : clazz.substring(0, idx);
    }

    private boolean checkJavaVersion() {
        return MINIMUM_JAVA_VERSION <= getJavaVersion();
    }

    private static int getJavaVersion() {
        String javaVersion = System.getProperty("java.specification.version");

        return Integer.parseInt(javaVersion);
    }

    public static final class BootStrapClassLoderProxy extends ClassLoader {

        private final TrafficHunterAgentClassLoader agentClassLoader;

        static {
            ClassLoader.registerAsParallelCapable();
        }

        public BootStrapClassLoderProxy(final TrafficHunterAgentClassLoader agentClassLoader) {
            super(null);
            this.agentClassLoader = agentClassLoader;
        }

        @Override
        public URL getResource(final String name) {

            URL url = super.getResource(name);
            if (url != null) {
                return url;
            }

            if (agentClassLoader != null) {
                JarEntry jarEntry = agentClassLoader.agentJarFile.getJarEntry(name);
                return agentClassLoader.getJarEntryUrl(jarEntry);
            }

            return null;
        }
    }

    private static class SpecificClassLoaderURLStreamHandler extends URLStreamHandler {

        private final JarFile jarFile;

        SpecificClassLoaderURLStreamHandler(final JarFile jarFile) {
            this.jarFile = jarFile;
        }

        static URLStreamHandler getStreamHandler(final JarFile jarFile) {
            return new SpecificClassLoaderURLStreamHandler(jarFile);
        }

        @Override
        protected URLConnection openConnection(final URL u) {
            return new SpecificClassLoaderURLConnection(u, jarFile);
        }
    }

    private static class SpecificClassLoaderURLConnection extends URLConnection {

        private final JarFile jarFile;
        private final String entryPath;
        private JarEntry jarEntry;

        SpecificClassLoaderURLConnection(final URL url, final JarFile jarFile) {
            super(url);
            this.jarFile = jarFile;
            String filePath = url.getFile();
            if(filePath.startsWith("/")) {
                filePath = filePath.substring(1);
            }

            if(filePath.isEmpty()) {
                filePath = null;
            }

            this.entryPath = filePath;
        }

        @Override
        public void connect() throws IOException {
            if(!connected) {
                if(entryPath != null) {
                    jarEntry = jarFile.getJarEntry(entryPath);
                    if(jarEntry == null) {
                        throw new FileNotFoundException("jar entry not found: " + entryPath);
                    }
                }

                connected = true;
            }
        }

        @Override
        public Permission getPermission() {
            return null;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            connect();

            if (entryPath == null) {
                throw new IOException("no entry name specified");
            } else {
                if (jarEntry == null) {
                    throw new FileNotFoundException(
                            "JAR entry " + entryPath + " not found in " + jarFile.getName());
                }
                return jarFile.getInputStream(jarEntry);
            }
        }

        @Override
        public long getContentLengthLong() {
            try {
                connect();

                if (jarEntry != null) {
                    return jarEntry.getSize();
                }
            } catch (IOException ignored) {}

            return -1;
        }

        @Override
        public int getContentLength() {
            return super.getContentLength();
        }
    }

    public BootStrapClassLoderProxy getBootstrapClassLoader() {
        return bootstrapClassLoader;
    }

    public JarFile getAgentJarFile() {
        return agentJarFile;
    }
}
