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
package ygo.traffic_hunter.common.util;

import java.util.Objects;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import ygo.traffic_hunter.common.web.interceptor.RequestPattern;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
public class MatcherUtil {

    private static final PathMatcher pathMatcher = new AntPathMatcher();

    public static boolean match(final RequestPattern requestPattern,
                                final RequestPattern compareRequestPattern) {

        return pathMatcher.match(requestPattern.uri(), compareRequestPattern.uri()) &&
                Objects.equals(requestPattern.httpMethod(), compareRequestPattern.httpMethod());
    }
}
