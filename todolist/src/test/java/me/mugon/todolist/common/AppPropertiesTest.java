package me.mugon.todolist.common;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class AppPropertiesTest {

    @Autowired
    AppProperties appProperties;

    @Test
    void propertiesTest() {
        assertEquals(appProperties.getTestEmail(), "user@email.com");
        assertEquals(appProperties.getTestPassword(), "password");
    }
}