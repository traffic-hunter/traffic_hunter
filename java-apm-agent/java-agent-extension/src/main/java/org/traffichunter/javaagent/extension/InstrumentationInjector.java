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
import java.lang.ref.WeakReference;
import java.net.URL;
import java.nio.file.Files;
import java.security.ProtectionDomain;
import java.security.SecureClassLoader;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;
import net.bytebuddy.agent.builder.AgentBuilder.Transformer;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.ClassFileLocator.Resolution;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.dynamic.loading.ClassInjector;
import net.bytebuddy.utility.JavaModule;
import org.traffichunter.javaagent.bootstrap.SharedClassLoaderManager;
import org.traffichunter.javaagent.bootstrap.TrafficHunterAgentClassLoader;
import org.traffichunter.javaagent.extension.bootstrap.TrafficHunterAgentStartAction;
import org.traffichunter.javaagent.plugin.sdk.cache.Cache;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
public class InstrumentationInjector implements Transformer {

    private static final Logger log = Logger.getLogger(InstrumentationInjector.class.getName());

    private static final ClassLoader BOOTSTRAP_CL_HOLDER;

    private static final Cache<Class<?>, Boolean> injectedClasses = Cache.weak();

    private static final String JAVA_EXTENSION_PREFIX = "extension.";

    static {

        BOOTSTRAP_CL_HOLDER = new SecureClassLoader(null) {

            @Override
            public String toString() {
                return "<bootstrap>";
            }
        };
    }

    private final String requestingName;

    private final Set<String> helperClassNames;
    private final Set<String> helperResourceNames;
    private final Map<String, byte[]> dynamicTypeMap = new LinkedHashMap<>();

    private final Cache<ClassLoader, Boolean> injectedClassLoaders = Cache.weak();

    private final List<WeakReference<JavaModule>> helperModules = new CopyOnWriteArrayList<>();

    public InstrumentationInjector(final String requestingName,
                                   final List<String> helperClassNames,
                                   final List<String> helperResourceNames) {

        this.requestingName = requestingName;
        this.helperClassNames = new LinkedHashSet<>(helperClassNames);
        this.helperResourceNames = new LinkedHashSet<>(helperResourceNames);
    }

    public InstrumentationInjector(final String requestingName, final Map<String, byte[]> map) {
        this.requestingName = requestingName;

        helperClassNames = map.keySet();
        dynamicTypeMap.putAll(map);

        helperResourceNames = Collections.emptySet();
    }


    public InstrumentationInjector forDynamicType(final String requestingName,
                                                  final List<DynamicType.Unloaded<?>> clazz) {

        Map<String, byte[]> bytes = new HashMap<>(clazz.size());

        for(DynamicType.Unloaded<?> helperClazz : clazz) {
            bytes.put(helperClazz.getTypeDescription().getName(), helperClazz.getBytes());
        }

        return new InstrumentationInjector(requestingName, bytes);
    }

    @Override
    public Builder<?> transform(Builder<?> builder,
                                TypeDescription typeDescription,
                                ClassLoader classLoader,
                                JavaModule javaModule,
                                ProtectionDomain protectionDomain) {

        if(!helperClassNames.isEmpty()) {
            if(classLoader.getParent() == null) {
                classLoader = BOOTSTRAP_CL_HOLDER;
            }

            if(!injectedClassLoaders.containsKey(classLoader)) {
                log.info("Injecting classloader " + classLoader + " " + helperClassNames);

                try {
                    Map<String, byte[]> classAndByteMap = getClassAndByteMap();
                    Map<String, Class<?>> clazzMap;

                    if(classLoader == BOOTSTRAP_CL_HOLDER) {
                        clazzMap = injectBootstrapClassLoader(classAndByteMap);
                    } else {
                        clazzMap = injectClassLoader(classLoader, classAndByteMap);
                    }

                    clazzMap.values().forEach(clazz -> injectedClasses.put(clazz, true));

                    if(JavaModule.isSupported()) {
                        JavaModule module = JavaModule.ofType(clazzMap.values().iterator().next());

                        helperModules.add(new WeakReference<>(module));
                    }
                } catch (Exception e) {
                    log.severe("error injecting classloader " + classLoader + " " + requestingName + " " + typeDescription + " " + e);
                    throw new RuntimeException(e);
                }

                injectedClassLoaders.put(classLoader, true);
            }

            ensureModuleCanReadHelperModules(javaModule);

            if(!helperResourceNames.isEmpty()) {
                for(String resourceName : helperResourceNames) {
                    URL url = TrafficHunterAgentClassLoader.class.getClassLoader().getResource(resourceName);

                    if(url == null) {
                        log.info("resource url not found : " + resourceName);
                        continue;
                    }

                    log.info("Injecting class loader = " + classLoader + " -> " + resourceName);
                    SharedClassLoaderManager.create(classLoader, resourceName, url);
                }
            }
        }

        return builder;
    }

    private Map<String, byte[]> getClassAndByteMap() throws IOException {
        if(dynamicTypeMap.isEmpty()) {
            Map<String, byte[]> map = new LinkedHashMap<>();

            ClassFileLocator classFileLocator = ClassFileLocator.ForClassLoader.of(Utilizr.getAgentClassLoader());

            for (String helperClassName : helperClassNames) {
                Resolution resolution = classFileLocator.locate(helperClassName);

                byte[] byteClass = resolution.resolve();

                map.put(helperClassName, byteClass);
            }

            return map;
        }

        return dynamicTypeMap;
    }

    private void ensureModuleCanReadHelperModules(final JavaModule module) {

        if(JavaModule.isSupported() && module != JavaModule.UNSUPPORTED && module.isNamed()) {
            for(WeakReference<JavaModule> moduleReference: helperModules) {

                Object existModule = Objects.requireNonNull(moduleReference.get()).unwrap();

                JavaModule javaModule = JavaModule.of(existModule);

                if(!module.canRead(javaModule)) {

                    ClassInjector.UsingInstrumentation.redefineModule(
                            TrafficHunterAgentStartAction.getInstrumentation(),
                            module,
                            Collections.singleton(javaModule),
                            Collections.emptyMap(),
                            Collections.emptyMap(),
                            Collections.emptySet(),
                            Collections.emptyMap()
                    );
                }
            }
        }
    }

    private Map<String, Class<?>> injectBootstrapClassLoader(final Map<String, byte[]> classAndByteMap) throws IOException {

        File tempDir = Files.createTempDirectory("traffichunter-temp").toFile();

        try {

            return ClassInjector.UsingInstrumentation.of(
                    tempDir,
                    ClassInjector.UsingInstrumentation.Target.BOOTSTRAP,
                    TrafficHunterAgentStartAction.getInstrumentation()
                    ).injectRaw(classAndByteMap);
        } finally {

            if(!tempDir.delete()) {
                tempDir.deleteOnExit();
            }
        }
    }

    private Map<String, Class<?>> injectClassLoader(final ClassLoader classLoader,
                                                    final Map<String, byte[]> classAndByteMap) {

        return new ClassInjector.UsingReflection(classLoader).injectRaw(classAndByteMap);
    }

    public String getRequestingName() {
        return requestingName;
    }
}
