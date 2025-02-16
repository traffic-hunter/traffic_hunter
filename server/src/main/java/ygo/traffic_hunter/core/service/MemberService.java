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
package ygo.traffic_hunter.core.service;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ygo.traffic_hunter.core.dto.response.member.MemberResponse;
import ygo.traffic_hunter.core.identification.login.LoginHandler;
import ygo.traffic_hunter.core.repository.MemberRepository;
import ygo.traffic_hunter.domain.entity.user.Member;
import ygo.traffic_hunter.domain.entity.user.Role;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    private final LoginHandler loginHandler;

    @Transactional
    public void signUp(final String email,
                       final String password,
                       final boolean isAlarm) {

        Member member = Member.builder()
                .email(email)
                .password(password)
                .isAlarm(isAlarm)
                .role(Role.USER)
                .build();

        memberRepository.save(member);
    }

    public MemberResponse signIn(final HttpServletRequest request,
                                 final String email,
                                 final String password) {

        Member member = memberRepository.findByEmailAndPassword(email, password);

        loginHandler.login(request, member.getId());

        return MemberResponse.map(member);
    }

    public void signOut(final HttpServletRequest request) {

        loginHandler.logout(request);
    }

    public MemberResponse findById(final Integer id) {

        Member member = memberRepository.findById(id);

        return MemberResponse.map(member);
    }

    public List<MemberResponse> findAll() {

        List<Member> members = memberRepository.findAll();

        return members.stream()
                .map(MemberResponse::map)
                .toList();
    }

    @Transactional
    public void update(final Integer id,
                       final String email,
                       final String password,
                       final boolean isAlarm) {

        Member updateMember = Member.builder()
                .id(id)
                .email(email)
                .password(password)
                .isAlarm(isAlarm)
                .role(Role.USER)
                .build();

        memberRepository.update(updateMember);
    }

    @Transactional
    public void delete(final Integer id) {

        memberRepository.deleteById(id);
    }
}
