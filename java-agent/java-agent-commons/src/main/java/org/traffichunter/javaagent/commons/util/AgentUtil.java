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
package org.traffichunter.javaagent.commons.util;

import java.net.URI;

/**
 * The {@code AgentUtil} enum provides utility methods for constructing
 * WebSocket and HTTP URLs for the TrafficHunter Agent. It also includes
 * methods for validating and formatting server addresses.
 *
 * @author yungwang-o
 * @version 1.0.0
*/
public enum AgentUtil {
    WEBSOCKET_URL("ws://%s/traffic-hunter/tx"),
    HTTP_URL("http://%s/traffic-hunter"),
    ;

    private static final String pattern = "^(localhost|\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}):\\d{1,5}$";

    private final String url;

    AgentUtil(final String url) {
        this.url = url;
    }

    public static boolean isAddr(final String serverUrl) {
        return serverUrl.matches(pattern);
    }

    public String getUrl(final String serverUrl) {
        return String.format(url, serverUrl);
    }

    public URI getUri(final String serverUrl) {
        return URI.create(String.format(url, serverUrl));
    }
}
