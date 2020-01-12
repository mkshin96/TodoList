package me.mugon.todolist.todolist;

import me.mugon.todolist.common.BaseControllerTest;
import me.mugon.todolist.domain.Account;
import me.mugon.todolist.domain.enums.AccountRole;
import me.mugon.todolist.domain.TodoList;
import me.mugon.todolist.domain.dto.TodoListDto;
import me.mugon.todolist.repository.AccountRepository;
import me.mugon.todolist.repository.TodoListRepository;
import me.mugon.todolist.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Random;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TodoListControllerTest extends BaseControllerTest {

    @Autowired
    private TodoListRepository tdlRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepo;

    @Autowired
    private PasswordEncoder encoder;

    private final String todoUri = "/todolists";

    @BeforeEach
    void setUp() {
        tdlRepo.deleteAll();
        accountRepo.deleteAll();
    }

    @Test
    @DisplayName("todo 조회하기")
    public void get_todo() throws Exception {
        Account account = generateAccount();
        IntStream.rangeClosed(1, 30).forEach(i -> createTdl(account));

        mockMvc.perform(get(todoUri)
                        .with(user(generateUserDetails())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("/tdl/list"));
    }

    @Test
    @DisplayName("정상적으로 todo 생성하기")
    void create_todo() throws Exception {
        //GIVEN
        TodoListDto tdlDto = new TodoListDto();
        tdlDto.setDescription("자기소개서 쓰기");

        //WHEN, THEN
        mockMvc.perform(post(todoUri)
                        .with(user(generateUserDetails_need_generateAccount()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tdlDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("description").value("자기소개서 쓰기"))
                .andExpect(jsonPath("status").value(false))
                .andExpect(jsonPath("createdAt").isNotEmpty())
                .andExpect(jsonPath("updatedAt").isEmpty());
    }

    @DisplayName("description이 공백인 todo생성 요청 시 Bad Request반환")
    @ParameterizedTest(name = "{displayName}{index}")
    @ValueSource(strings = {"", "            "})
    void create_todo_bad_request_blank_value(String description) throws Exception {
        //GIVEN
        TodoListDto tdlDto = new TodoListDto();
        tdlDto.setDescription(description);

        //WHEN, THEN
        mockMvc.perform(post(todoUri)
                        .with(user(generateUserDetails_need_generateAccount()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tdlDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("[*].objectName").exists())
                .andExpect(jsonPath("[*].field").exists())
                .andExpect(jsonPath("[*].code").exists())
                .andExpect(jsonPath("[*].defaultMessage").exists());
    }

    @DisplayName("255자 초과의 todo생성 요청 시 Bad Request 반환")
    @Test
    void create_todo_bad_request_long_value() throws Exception {
        String longLengthString = generateLongString();

        TodoListDto tdlDto = new TodoListDto();
        tdlDto.setDescription(longLengthString);

        mockMvc.perform(post(todoUri)
                            .with(user(generateUserDetails_need_generateAccount()))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(tdlDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("[*].objectName").exists())
                .andExpect(jsonPath("[*].field").exists())
                .andExpect(jsonPath("[*].code").exists())
                .andExpect(jsonPath("[*].defaultMessage").exists());
    }

    @Test
    @DisplayName("정상적으로 todo 수정하기")
    void update_todo() throws Exception {
        Account account = generateAccount();
        TodoList savedTdl = createTdl(account);
        TodoListDto tdlDto = modelMapper.map(savedTdl, TodoListDto.class);
        tdlDto.setDescription("그냥 집에서 쉬기");

        mockMvc.perform(put(todoUri + "/{id}", savedTdl.getId())
                            .with(user(generateUserDetails()))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(tdlDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("description").value("그냥 집에서 쉬기"))
                .andExpect(jsonPath("status").value(false))
                .andExpect(jsonPath("createdAt").exists())
                .andExpect(jsonPath("updatedAt").isNotEmpty());
    }

    @DisplayName("description이 공백인 todo 수정 요청 시 Bad Request반환")
    @ParameterizedTest(name = "{displayName}{index}")
    @ValueSource(strings = {"", "            "})
    void update_todo_bad_request_blank_value(String description) throws Exception {
        Account account = generateAccount();
        TodoList savedTdl = createTdl(account);
        TodoListDto tdlDto = modelMapper.map(savedTdl, TodoListDto.class);
        tdlDto.setDescription(description);

        mockMvc.perform(put(todoUri + "/{id}", savedTdl.getId())
                            .with(user(generateUserDetails()))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(tdlDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("[*].objectName").exists())
                .andExpect(jsonPath("[*].field").exists())
                .andExpect(jsonPath("[*].code").exists())
                .andExpect(jsonPath("[*].defaultMessage").exists());
    }

    @DisplayName("255자 초과의 todo 수정 요청 시 Bad Request 반환")
    @Test
    void update_todo_bad_request_long_value() throws Exception {
        String longLengthString = generateLongString();
        Account account = generateAccount();
        TodoList savedTdl = createTdl(account);
        TodoListDto tdlDto = modelMapper.map(savedTdl, TodoListDto.class);
        tdlDto.setDescription(longLengthString);

        mockMvc.perform(put(todoUri + "/{id}", savedTdl.getId())
                            .with(user(generateUserDetails()))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(tdlDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("[*].objectName").exists())
                .andExpect(jsonPath("[*].field").exists())
                .andExpect(jsonPath("[*].code").exists())
                .andExpect(jsonPath("[*].defaultMessage").exists());
    }

    @Test
    @DisplayName("정상적으로 todo 삭제하기")
    void delete_todo() throws Exception {
        Account account = generateAccount();
        TodoList savedTdl = createTdl(account);

        mockMvc.perform(delete(todoUri + "/{id}", savedTdl.getId())
                        .with(user(generateUserDetails())))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("삭제하려는 todo의 id가 저장되어 있지 않을 때")
    void delete_todo_not_found() throws Exception {
        Account account = generateAccount();
        createTdl(account);
        mockMvc.perform(delete(todoUri + "/{id}", Long.MIN_VALUE)
                        .with(user(generateUserDetails())))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("정상적으로 todo 완료시키기")
    void complete_todo() throws Exception {
        Account account = generateAccount();
        TodoList savedTdl = createTdl(account);

        //완료 요청
        mockMvc.perform(put(todoUri + "/{id}/complete", savedTdl.getId())
                .with(user(generateUserDetails())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("description").value("운동 하기"))
                .andExpect(jsonPath("status").value(true))
                .andExpect(jsonPath("createdAt").exists())
                .andExpect(jsonPath("updatedAt").isEmpty())
                .andExpect(jsonPath("completedAt").isNotEmpty());

        //미완료 미완료 요청
        mockMvc.perform(put(todoUri + "/{id}/complete", savedTdl.getId())
                .with(user(generateUserDetails())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("description").value("운동 하기"))
                .andExpect(jsonPath("status").value(false))
                .andExpect(jsonPath("createdAt").exists())
                .andExpect(jsonPath("updatedAt").isEmpty())
                .andExpect(jsonPath("completedAt").isEmpty());
    }

    @Test
    @DisplayName("tood 완료 요청 시 존재하지 않는 todo일 경우 Bad Request 반환")
    void complete_todo__not_exits() throws Exception {
        Account account = generateAccount();
        createTdl(account);

        mockMvc.perform(put(todoUri + "/{id}/complete", Long.MIN_VALUE)
                .with(user(generateUserDetails())))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").value("존재하지 않는 todo입니다."));
    }

    private TodoList createTdl(Account account) {
        TodoList tdl = TodoList.builder()
                .description("운동 하기")
                .status(false)
                .createdAt(LocalDateTime.now())
                .account(account)
                .build();
        return tdlRepo.save(tdl);
    }

    private String generateLongString() {
        int zero = 48, z = 122, targetStringLength = 256;
        Random random = new Random();
        String longRandomString = random.ints(zero, z + 1)
                .filter(i -> (i <= 57 || i >= 67) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        assertEquals(longRandomString.length(), 256, "랜덤 문자열의 길이가 256이 아닙니다.");

        return longRandomString;
    }

    private UserDetails generateUserDetails_need_generateAccount() {
        generateAccount();
        return this.accountService.loadUserByUsername(appProperties.getTestEmail());
    }

    private UserDetails generateUserDetails() {
        return this.accountService.loadUserByUsername(appProperties.getTestEmail());
    }

    private Account generateAccount() {
        return accountRepo.save(
                Account.builder()
                .email(appProperties.getTestEmail())
                .password(encoder.encode(appProperties.getTestPassword()))
                .createdAt(LocalDateTime.now())
                .accountRoles(Collections.singletonList(AccountRole.USER))
                .build()
        );
    }
}