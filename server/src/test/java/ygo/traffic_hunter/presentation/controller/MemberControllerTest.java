package ygo.traffic_hunter.presentation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseBody;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ygo.traffic_hunter.AbstractTestConfiguration;
import ygo.traffic_hunter.common.util.LoginUtils;
import ygo.traffic_hunter.common.web.interceptor.LoginInterceptor;
import ygo.traffic_hunter.common.web.interceptor.RequestMatcherInterceptor;
import ygo.traffic_hunter.common.web.resolver.MemberArgumentResolver;
import ygo.traffic_hunter.core.dto.request.member.SignIn;
import ygo.traffic_hunter.core.dto.request.member.SignUp;
import ygo.traffic_hunter.core.dto.request.member.UpdateMember;
import ygo.traffic_hunter.core.dto.response.member.MemberResponse;
import ygo.traffic_hunter.core.service.MemberService;
import ygo.traffic_hunter.domain.entity.user.Role;
import ygo.traffic_hunter.persistence.impl.MemberRepositoryImpl.MemberNotFoundException;
import ygo.traffic_hunter.presentation.advice.GlobalControllerAdvice;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = MemberController.class)
class MemberControllerTest extends AbstractTestConfiguration {

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    MemberService memberService;

    @Autowired
    LoginInterceptor loginInterceptor;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {

        RequestMatcherInterceptor requestMatcherInterceptor = new RequestMatcherInterceptor(loginInterceptor);
        requestMatcherInterceptor.addIncludingRequestPattern("/**")
                .addExcludingRequestPattern("/members", HttpMethod.POST)
                .addExcludingRequestPattern("/**/sign-in", HttpMethod.POST);


        this.mockMvc = MockMvcBuilders.standaloneSetup(new MemberController(memberService))
                .apply(documentationConfiguration(restDocumentation))
                .setCustomArgumentResolvers(new MemberArgumentResolver())
                .setControllerAdvice(new GlobalControllerAdvice())
                .addInterceptors(requestMatcherInterceptor)
                .build();
    }

    @Test
    void 회원_가입은_정상적으로_동작한다_201() throws Exception {
        // given
        String email = "qwer1234@naver.com";
        String password = "qwer1234";

        SignUp signUp = new SignUp(email, password);

        willDoNothing()
                .given(memberService)
                .signUp(email, password, true);

        // when
        ResultActions resultActions = mockMvc.perform(post("/members")
                .content(objectMapper.writeValueAsString(signUp))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isCreated())
                .andDo(print())
                .andDo(document("members/sign-up"
                        , preprocessRequest(prettyPrint())
                        , preprocessResponse(prettyPrint())
                        , requestFields(
                                fieldWithPath("email").description("이메일"),
                                fieldWithPath("password").description("비밀 번호")
                        )
                        , responseBody()
                ));
    }

    @Test
    void 회원_유효성_검사에_실패한다_400() throws Exception {
        // given
        String badEmail = "qwer1234";
        String password = "qwer1234";

        SignUp signUp = new SignUp(badEmail, password);

        willDoNothing()
                .given(memberService)
                .signUp(badEmail, password, true);

        // when
        ResultActions resultActions = mockMvc.perform(post("/members")
                .content(objectMapper.writeValueAsString(signUp))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isBadRequest())
                .andDo(print())
                .andDo(document("members/failed-sign-up"
                        , preprocessRequest(prettyPrint())
                        , preprocessResponse(prettyPrint())
                ));
    }

    @Test
    void 로그인은_정상적으로_동작한다_200() throws Exception {
        // given
        SignIn signIn = new SignIn("test@example.com", "password123");

        MemberResponse response = new MemberResponse("test@example.com", true, Role.USER);

        given(memberService.signIn(any(HttpServletRequest.class), eq(signIn.email()), eq(signIn.password())))
                .willReturn(response);

        // When & Then
        mockMvc.perform(post("/members/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signIn)))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("members/sign-in",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("email").description("회원의 이메일 주소"),
                                fieldWithPath("password").description("회원의 비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("email").description("회원 이메일"),
                                fieldWithPath("isAlarm").description("알림 설정 여부"),
                                fieldWithPath("role").description("회원 역할")
                        )
                ));
    }

    @Test
    void 모든_회원_조회는_정상적으로_동작한다_200() throws Exception {
        // given
        List<MemberResponse> responses = Arrays.asList(
                new MemberResponse("test@example.com", true, Role.USER),
                new MemberResponse("example@test.com", false, Role.USER)
        );

        given(memberService.findAll()).willReturn(responses);

        // when & then:
        mockMvc.perform(get("/members").session(createMockSession())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("members/get-members",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[].email").description("회원 이메일"),
                                fieldWithPath("[].isAlarm").description("알림 설정 여부"),
                                fieldWithPath("[].role").description("회원 역할")
                        )
                ));
    }

    @Test
    void 회원_단건_조회는_정상적으로_동작한다_200() throws Exception {
        // given
        MemberResponse response = new MemberResponse("test@example.com", true, Role.USER);
        given(memberService.findById(1)).willReturn(response);

        // when & then
        mockMvc.perform(get("/members/{id}", 1).session(createMockSession())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("members/get-member",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("조회할 회원의 ID")
                        ),
                        responseFields(
                                fieldWithPath("email").description("회원 이메일"),
                                fieldWithPath("isAlarm").description("알림 설정 여부"),
                                fieldWithPath("role").description("회원 역할")
                        )
                ));
    }

    @Test
    void 존재하는_회원이_없다_404() throws Exception {
        // given
        given(memberService.findById(any()))
                .willThrow(new MemberNotFoundException("not found member"));

        // when & then
        mockMvc.perform(get("/members/{id}", 1).session(createMockSession())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(document("members/failed-get-member",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                ));
    }

    @Test
    void 로그아웃은_정상적으로_동작한다_204() throws Exception {
        // given
        willDoNothing().given(memberService).signOut(any(HttpServletRequest.class));

        // when & then
        mockMvc.perform(post("/members/sign-out").session(createMockSession()))
                .andExpect(status().isNoContent())
                .andDo(document("members/sign-out",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                ));
    }

    @Test
    void 회원_수정은_정상적으로_동작한다_204() throws Exception {
        // given
        UpdateMember updateMember = new UpdateMember(
                "new@example.com",
                "newPassword123",
                true
        );

        doNothing().when(memberService)
                .update(
                        eq(1),
                        eq(updateMember.email()),
                        eq(updateMember.password()),
                        eq(updateMember.isAlarm())
                );

        // when & then
        mockMvc.perform(put("/members").session(createMockSession())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Member", "1")
                        .content(objectMapper.writeValueAsString(updateMember)))
                .andExpect(status().isNoContent())
                .andDo(document("members/update",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Member").description("회원 ID (커스텀 리졸버를 통해 처리)")
                        ),
                        requestFields(
                                fieldWithPath("email").description("수정할 회원 이메일"),
                                fieldWithPath("password").description("수정할 회원 비밀번호"),
                                fieldWithPath("isAlarm").description("수정할 알림 설정 여부")
                        )
                ));
    }

    @Test
    void 회원_삭제는_정상적으로_동작한다_204() throws Exception {
        // given
        willDoNothing().given(memberService).delete(1);

        // when & then
        mockMvc.perform(delete("/members").session(createMockSession())
                        .header("Member", "1"))
                .andExpect(status().isNoContent())
                .andDo(document("members/delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Member").description("삭제할 회원 ID (커스텀 리졸버를 통해 처리)")
                        )
                ));
    }

    @Test
    void 회원이_로그인_되어있다_200() throws Exception {
        // given
        MemberResponse response = new MemberResponse("test@example.com", true, Role.USER);
        given(memberService.findById(1)).willReturn(response);

        // when & then
        mockMvc.perform(get("/members/check")
                        .session(createMockSession()))
                .andExpect(status().isOk())
                .andDo(document("members/check",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("email").description("회원 이메일"),
                                fieldWithPath("isAlarm").description("알림 설정 여부"),
                                fieldWithPath("role").description("회원 역할")
                        ))
                );

    }

    @Test
    void 회원이_로그인_되어있지_않다_401() throws Exception {


        mockMvc.perform(get("/members/check"))
                .andExpect(status().isUnauthorized())
                .andDo(document("members/failed-check",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("type").description("문제 유형을 나타내는 URL (일반적으로 'about:blank')"),
                                fieldWithPath("title").description("문제의 제목 (예: 'Unauthorized')"),
                                fieldWithPath("status").description("HTTP 상태 코드 (401 Unauthorized)"),
                                fieldWithPath("detail").description("문제에 대한 상세 설명 (예: 'Login is required')")
                        ))
                );
    }

    private MockHttpSession createMockSession() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(LoginUtils.SESSION_ID.name(), 1);
        return session;
    }


}