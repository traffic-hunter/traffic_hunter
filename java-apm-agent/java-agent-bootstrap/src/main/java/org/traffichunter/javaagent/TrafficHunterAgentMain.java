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
package org.traffichunter.javaagent;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import org.traffichunter.javaagent.bootstrap.AgentExecutionEngine;
import org.traffichunter.javaagent.bootstrap.BootState;

/**
 * Agent main entry point.
 * @author yungwang-o
 * @version 1.0.0
 */
public class TrafficHunterAgentMain {

    private static final BootState STATE = new BootState();

    public static void premain(String agentArgs, Instrumentation inst) {
        start(inst);
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        start(inst);
    }

    private static void start(final Instrumentation inst) {

        final boolean success = STATE.start();
        if(!success) {
            System.err.println("Failed to start agent");
            return;
        }

        File agentFile = loadBootstrapJar(inst);
        AgentExecutionEngine.run(agentFile, getSystemEnvConfig(), inst);
    }

    private static File loadBootstrapJar(final Instrumentation inst) {

        ClassLoader classLoader = TrafficHunterAgentMain.class.getClassLoader();

        if(classLoader == null) {
            classLoader = ClassLoader.getSystemClassLoader();
        }

        URL url = classLoader.getResource(
                TrafficHunterAgentMain.class.getName().replace('.', '/') + ".class"
        );

        try {
            File agentFile = getAgentFile(url);

            JarFile agentJarFile = new JarFile(agentFile, false);
            verifyManifestBootstrapJar(agentJarFile);
            inst.appendToBootstrapClassLoaderSearch(agentJarFile);

            return agentFile;
        } catch (IOException | URISyntaxException e) {
            throw new IllegalStateException("Failed to load bootstrap jar", e);
        }
    }

    private static File getAgentFile(final URL url) throws URISyntaxException {

        if (url == null || !"jar".equals(url.getProtocol())) {
            throw new IllegalStateException("Unable to find traffic hunter agent jar");
        }

        String resource = url.toURI().getSchemeSpecificPart();
        int protocolIndex = resource.indexOf(":");
        int resourceIndex = resource.indexOf("!/");
        if (protocolIndex == -1 || resourceIndex == -1) {
            throw new IllegalStateException("could not get agent location from url " + url);
        }

        String agentPath = resource.substring(protocolIndex + 1, resourceIndex);
        return new File(agentPath);
    }

    private static void verifyManifestBootstrapJar(final JarFile agentJarFile) throws IOException {

        Manifest manifest = agentJarFile.getManifest();

        if(manifest.getMainAttributes().getValue("Premain-Class") == null) {
            throw new IllegalStateException("This agent not load because Premain-Class manifest not present");
        }
    }

    private static String getSystemEnvConfig() {
        return "traffichunter.config";
    }
}
