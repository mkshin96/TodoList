package me.mugon.todolist.account;

import me.mugon.todolist.common.BaseControllerTest;
import me.mugon.todolist.domain.Account;
import me.mugon.todolist.domain.dto.AccountDto;
import me.mugon.todolist.domain.enums.AccountRole;
import me.mugon.todolist.repository.AccountRepository;
import me.mugon.todolist.repository.TodoListRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AccountControllerTest extends BaseControllerTest {

    @Autowired
    private AccountRepository accountRepo;

    @Autowired
    private TodoListRepository tdlRepo;

    @Autowired
    private ModelMapper modelMapper;

    String userUri = "/users";

    @BeforeEach
    void setUp() {
        this.tdlRepo.deleteAll();
        this.accountRepo.deleteAll();
    }

    @Test
    @DisplayName("정상적으로 유저를 생성")
    void saveAccount() throws Exception {
        AccountDto accountDto = new AccountDto();
        accountDto.setEmail(appProperties.getTestEmail());
        accountDto.setPassword(appProperties.getTestPassword());

        mockMvc.perform(post(userUri)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(accountDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("email").value(appProperties.getTestEmail()))
                .andExpect(jsonPath("password").doesNotExist())
                .andExpect(jsonPath("createdAt").exists())
                .andExpect(jsonPath("accountRoles").exists());

        Optional<Account> accountOptional = accountRepo.findByEmail(appProperties.getTestEmail());
        Account account = accountOptional.orElseGet(() -> fail("유저가 생성되지 않았습니다."));

        assertEquals(account.getEmail(), appProperties.getTestEmail());
        assertNotNull(account.getCreatedAt());
    }

    @Test
    @DisplayName("이미 존재하는 email로 유저 생성을 요청했을 때 에러 메시지와 함께 Bad Request를 반환하는 테스트")
    void saveAccount_already_exist_email() throws Exception {
        AccountDto accountDto = new AccountDto();
        accountDto.setEmail(appProperties.getTestEmail());
        accountDto.setPassword(appProperties.getTestPassword());

        Account account = modelMapper.map(accountDto, Account.class);
        accountRepo.save(account);

        mockMvc.perform(post(userUri + "/checkEmail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountDto)))
                        .andDo(print())
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("message").value("이미 존재하는 email입니다."));
    }
}