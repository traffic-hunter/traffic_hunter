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
package ygo.traffic_hunter.core.identification.login.session;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ygo.traffic_hunter.common.util.LoginUtils;
import ygo.traffic_hunter.core.identification.login.LoginHandler;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
@Component
@RequiredArgsConstructor
public class SessionBasedLoginHandler implements LoginHandler {

    @Override
    public void login(final HttpServletRequest request, final Integer id) {

        request.getSession().invalidate();
        HttpSession session = request.getSession(true);

        session.setAttribute(LoginUtils.SESSION_ID.name(), id);
        session.setMaxInactiveInterval(Integer.MAX_VALUE);
    }

    @Override
    public void logout(final HttpServletRequest request) {

        HttpSession session = request.getSession(false);

        if(!Objects.isNull(session)) {
            session.invalidate();
        }
    }
}
