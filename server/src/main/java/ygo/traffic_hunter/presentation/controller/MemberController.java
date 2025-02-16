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
package ygo.traffic_hunter.presentation.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ygo.traffic_hunter.core.annotation.Member;
import ygo.traffic_hunter.core.dto.request.member.SignIn;
import ygo.traffic_hunter.core.dto.request.member.SignUp;
import ygo.traffic_hunter.core.dto.request.member.UpdateMember;
import ygo.traffic_hunter.core.dto.response.member.MemberResponse;
import ygo.traffic_hunter.core.service.MemberService;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void signUpApi(@RequestBody @Valid final SignUp signUp) {

        memberService.signUp(signUp.email(), signUp.password(), true);
    }

    @PostMapping("/sign-in")
    @ResponseStatus(HttpStatus.OK)
    public MemberResponse signInApi(@RequestBody @Valid final SignIn signIn, final HttpServletRequest request) {

        return memberService.signIn(request, signIn.email(), signIn.password());
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<MemberResponse> getMembersApi() {

        return memberService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public MemberResponse getMemberApi(@PathVariable @NotNull final Integer id) {

        return memberService.findById(id);
    }

    @PostMapping("/sign-out")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void signOutApi(final HttpServletRequest request) {

        memberService.signOut(request);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateApi(@RequestBody @Valid final UpdateMember updateMember, @Member final Integer id) {

        memberService.update(
                id,
                updateMember.email(),
                updateMember.password(),
                updateMember.threshold(),
                updateMember.isAlarm()
        );
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteApi(@Member final Integer id) {

        memberService.delete(id);
    }
}
