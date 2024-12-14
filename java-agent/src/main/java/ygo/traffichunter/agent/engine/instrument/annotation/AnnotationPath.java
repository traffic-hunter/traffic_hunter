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
package ygo.traffichunter.agent.engine.instrument.annotation;

/**
 * The {@code AnnotationPath} enum defines commonly used annotation paths
 * for Spring-based applications, which are used for bytecode manipulation
 * with ByteBuddy.
 *
 * @author yungwang-o
 * @version 1.0.0
 */
public enum AnnotationPath {

    TRANSACTIONAL("org.springframework.transaction.annotation.Transactional"),
    SERVICE("org.springframework.stereotype.Service"),
    REPOSITORY("org.springframework.stereotype.Repository"),
    REST_CONTROLLER("org.springframework.web.bind.annotation.RestController"),
    CONTROLLER("org.springframework.stereotype.Controller"),
    ;

    private final String path;

    AnnotationPath(final String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public static boolean filter(final String path) {
        return path.equals("join") || path.equals("wait") || path.equals("notify") || path.equals("notifyAll") ||
                path.equals("hashcode") || path.equals("equals") || path.equals("toString");
    }
}
