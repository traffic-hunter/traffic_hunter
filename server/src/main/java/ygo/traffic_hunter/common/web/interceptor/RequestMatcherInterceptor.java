/**
 * The MIT License
 * <p>
 * Copyright (c) 2024 traffic-hunter.org
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package ygo.traffic_hunter.common.web.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
public class RequestMatcherInterceptor implements HandlerInterceptor {

    private final HandlerInterceptor handlerInterceptor;

    private final List<RequestPattern> includingRequestPattern = new ArrayList<>();

    private final List<RequestPattern> excludingRequestPattern = new ArrayList<>();

    public RequestMatcherInterceptor(HandlerInterceptor handlerInterceptor) {
        this.handlerInterceptor = handlerInterceptor;
    }

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler)
            throws Exception {

        if (isMatchingRequestUri(request)) {
            return handlerInterceptor.preHandle(request, response, handler);
        }

        return true;
    }

    private boolean isMatchingRequestUri(final HttpServletRequest request) {
        final String requestUri = request.getRequestURI();
        final HttpMethod httpMethod = HttpMethod.valueOf(request.getMethod().toUpperCase());

        final boolean isExcludingUri = excludingRequestPattern.stream()
                .anyMatch(requestPattern -> requestPattern.match(requestUri, httpMethod));

        if (isExcludingUri) {
            return false;
        }

        final boolean isIncludingUri = includingRequestPattern.stream()
                .anyMatch(requestPattern -> requestPattern.match(requestUri, httpMethod));

        return isIncludingUri;
    }

    public RequestMatcherInterceptor addIncludingRequestPattern(final String uri,
                                                                final HttpMethod... httpMethods) {

        List<HttpMethod> httpMethodList = Arrays.stream(httpMethods).collect(Collectors.toList());
        if (httpMethodList.isEmpty()) {
            httpMethodList = getDefaultHttpMethod();
        }

        httpMethodList.forEach(httpMethod -> this.includingRequestPattern.add(new RequestPattern(uri, httpMethod)));

        return this;
    }

    public RequestMatcherInterceptor addExcludingRequestPattern(final String uri, final HttpMethod... httpMethods) {

        List<HttpMethod> httpMethodList = Arrays.stream(httpMethods).collect(Collectors.toList());
        if (httpMethodList.isEmpty()) {
            httpMethodList = getDefaultHttpMethod();
        }

        httpMethodList.forEach(httpMethod -> this.excludingRequestPattern.add(new RequestPattern(uri, httpMethod)));

        return this;
    }

    private List<HttpMethod> getDefaultHttpMethod() {
        List<HttpMethod> httpMethodList = new ArrayList<>();
        httpMethodList.add(HttpMethod.GET);
        httpMethodList.add(HttpMethod.POST);
        httpMethodList.add(HttpMethod.PUT);
        httpMethodList.add(HttpMethod.DELETE);
        httpMethodList.add(HttpMethod.HEAD);
        httpMethodList.add(HttpMethod.OPTIONS);
        httpMethodList.add(HttpMethod.TRACE);
        httpMethodList.add(HttpMethod.PATCH);
        return httpMethodList;
    }

}
