package me.mugon.todolist.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class SecurityConfigTest {

    @Test
    void value_test(@Value("${custom.oauth2.kakao.client-id}") String clientId) {
        assertNotNull(clientId);
        assertEquals(clientId, "4236b1428dc75ac172f6063df9f0c5fe");
    }
}