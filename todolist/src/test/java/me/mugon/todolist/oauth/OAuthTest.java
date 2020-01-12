package me.mugon.todolist.oauth;

import me.mugon.todolist.common.BaseControllerTest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class OAuthTest extends BaseControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("kakao social login test")
    void kakao_login_test() throws Exception {
        mockMvc.perform(get("/oauth2/authorization/kakao") )
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", Matchers.containsString("https://kauth.kakao.com/oauth/authorize")));
    }

    @Test
    @DisplayName("facebook social login test")
    void facebook_login_test() throws Exception {
        mockMvc.perform(get("/oauth2/authorization/facebook"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", Matchers.containsString("https://www.facebook.com/v2.8/dialog/oauth")));
    }
}
