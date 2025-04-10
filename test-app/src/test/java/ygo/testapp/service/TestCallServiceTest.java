package ygo.testapp.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import ygo.testapp.service.TestCallService.Dto;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
@SpringBootTest
class TestCallServiceTest {

    @Autowired
    private TestCallService testCallService;

    @Test
    void test() {
        ResponseEntity<List<Dto>> test = testCallService.test();

        System.out.println(test.getBody());
    }
}