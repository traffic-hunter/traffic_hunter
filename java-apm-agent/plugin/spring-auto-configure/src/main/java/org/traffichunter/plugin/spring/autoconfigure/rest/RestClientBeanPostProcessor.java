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
package org.traffichunter.plugin.spring.autoconfigure.rest;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestClient;
import org.traffichunter.plugin.spring.insrumentation.SpringWebInstrumentation;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
final class RestClientBeanPostProcessor implements BeanPostProcessor {

    RestClientBeanPostProcessor() {}

    @Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
        if(bean instanceof RestClient restClient) {

            ClientHttpRequestInterceptor instanceInterceptor = SpringWebInstrumentation.instance();

            return restClient.mutate()
                    .requestInterceptors(interceptors -> {

                        boolean isMatch = interceptors.stream()
                                .noneMatch(interceptor -> instanceInterceptor.getClass() == interceptor.getClass());

                        if(isMatch) {
                            interceptors.addFirst(instanceInterceptor);
                        }
                    })
                    .build();
        }

        return bean;
    }
}
