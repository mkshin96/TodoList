package me.mugon.todolist.account;

import me.mugon.todolist.common.BaseControllerTest;
import me.mugon.todolist.domain.dto.AccountDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AccountControllerTest extends BaseControllerTest {


    @Test
    @DisplayName("정상적으로 유저를 생성")
    void saveAccount() throws Exception {
        AccountDto accountDto = new AccountDto();
        accountDto.setEmail(appProperties.getTestEmail());
        accountDto.setPassword(appProperties.getTestPassword());

        mockMvc.perform(post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(accountDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("email").value(appProperties.getTestEmail()))
                .andExpect(jsonPath("password").doesNotExist())
                .andExpect(jsonPath("createdAt").exists())
                .andExpect(jsonPath("accountRoles").exists());

    }
}