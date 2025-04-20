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
package ygo.traffic_hunter.common.util;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import ygo.traffic_hunter.AbstractTestConfiguration;
import ygo.traffic_hunter.common.web.interceptor.RequestPattern;

/**
 * @author JuSeong
 * @version 1.1.0
 */
public class MatcherUtilTest extends AbstractTestConfiguration {


    @Test
    void 요청_주소가_일치한다면_True() {

        // given
        RequestPattern requestPattern = new RequestPattern("/members/**", HttpMethod.POST);
        RequestPattern compareRequestPattern = new RequestPattern("/members/check", HttpMethod.POST);

        //when & then
        Assertions.assertThat(MatcherUtil.match(requestPattern, compareRequestPattern)).isTrue();
    }

    @Test
    void 요청_주소가_일치하지않으면_False() {

        // given
        RequestPattern requestPattern = new RequestPattern("/members/**", HttpMethod.POST);
        RequestPattern compareRequestPattern = new RequestPattern("/metrics/check", HttpMethod.POST);

        //when & then
        Assertions.assertThat(MatcherUtil.match(requestPattern, compareRequestPattern)).isFalse();
    }


}
