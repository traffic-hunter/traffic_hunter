package ygo.testapp.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ygo.testapp.dto.TestDto;
import ygo.testapp.service.TestCallService;
import ygo.testapp.service.TestCallService.Dto;
import ygo.testapp.service.TestService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;
    private final TestCallService testCallService;

    @PostMapping("/test")
    @ResponseStatus(HttpStatus.OK)
    public TestDto saveApi(@RequestBody TestDto testDto) {
        return testService.join(testDto.name(), testDto.email());
    }

    @GetMapping("/test/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TestDto findByIdApi(@PathVariable Long id) {
        return testService.findById(id);
    }

    @GetMapping("/test/call")
    @ResponseStatus(HttpStatus.OK)
    public List<Dto> callApi() {

        log.info("call");
        return testCallService.test().getBody();
    }

    @GetMapping("/test")
    @ResponseStatus(HttpStatus.OK)
    public List<TestDto> findAllApi() {
        return testService.findAll();
    }

    @GetMapping("/test/name/{name}")
    @ResponseStatus(HttpStatus.OK)
    public TestDto findByNameApi(@PathVariable String name) {
        return testService.findByName(name);
    }

    @GetMapping("/test/email/{email}")
    @ResponseStatus(HttpStatus.OK)
    public TestDto findByEmailApi(@PathVariable String email) {
        return testService.findByEmail(email);
    }
}
