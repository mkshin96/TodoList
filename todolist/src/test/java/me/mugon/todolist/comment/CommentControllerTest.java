package me.mugon.todolist.comment;

import me.mugon.todolist.common.BaseControllerTest;
import me.mugon.todolist.domain.Account;
import me.mugon.todolist.domain.AccountRole;
import me.mugon.todolist.domain.TodoList;
import me.mugon.todolist.domain.dto.CommentDto;
import me.mugon.todolist.repository.AccountRepository;
import me.mugon.todolist.repository.CommentRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CommentControllerTest extends BaseControllerTest {

    @Autowired
    AccountRepository accountRepo;

    @Autowired
    AccountService accountService;

    @Autowired
    TodoListRepository tdlRepo;

    @Autowired
    CommentRepository commentRepo;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    PasswordEncoder encoder;

    private final String commentUri = "/comments";

    @BeforeEach
    void setUp() {
        commentRepo.deleteAll();
        tdlRepo.deleteAll();
        accountRepo.deleteAll();
    }

    @Test
    @DisplayName("정상적으로 댓글을 생성")
    void saveComment() throws Exception {
        //todo 생성
        TodoList newTodo = generateTodo();
        CommentDto commentDto = new CommentDto();
        commentDto.setBody("안녕하세요. 댓글입니다.");

        mockMvc.perform(post(commentUri + "/{todoListId}", newTodo.getId())
                        .with(user(generateUserDetails()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("body").exists())
                .andExpect(jsonPath("createdAt").exists())
                .andExpect(jsonPath("updatedAt").isEmpty())
                .andExpect(jsonPath("todoList").isNotEmpty());
    }

    @ParameterizedTest(name = "{displayName}{index}")
    @ValueSource(strings = {"", "               "})
    @DisplayName("body가 공백인 댓글 생성 요청 시 Bad Request 반환")
    void saveComment_empty_value_bad_request(String body) throws Exception {
        TodoList newTodo = generateTodo();
        CommentDto commentDto = new CommentDto();
        commentDto.setBody(body);

        mockMvc.perform(post(commentUri + "/{todoListId}", newTodo.getId())
                            .with(user(generateUserDetails()))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(commentDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("[*].defaultMessage").exists())
                .andExpect(jsonPath("[*].code").exists())
                .andExpect(jsonPath("[*].objectName").exists())
                .andExpect(jsonPath("[*].field").exists());
    }

    @Test
    @DisplayName("body가 null인 댓글 생성 요청 시 Bad Request 반환")
    void saveComment_null_value_bad_request() throws Exception {
        TodoList newTodo = generateTodo();
        CommentDto commentDto = new CommentDto();
        commentDto.setBody(null);

        mockMvc.perform(post(commentUri + "/{todoListId}", newTodo.getId())
                .with(user(generateUserDetails()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("[*].defaultMessage").exists())
                .andExpect(jsonPath("[*].code").exists())
                .andExpect(jsonPath("[*].objectName").exists())
                .andExpect(jsonPath("[*].field").exists());
    }

    @Test
    @DisplayName("body가 255자 초과인 댓글 생성 요청 시 Bad Request 반환")
    void saveComment_longer_than_255_bad_request() throws Exception {
        TodoList newTodo = generateTodo();
        CommentDto commentDto = new CommentDto();
        commentDto.setBody(generateLongString());

        mockMvc.perform(post(commentUri + "/{todoListId}", newTodo.getId())
                .with(user(generateUserDetails()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("[*].defaultMessage").exists())
                .andExpect(jsonPath("[*].code").exists())
                .andExpect(jsonPath("[*].objectName").exists())
                .andExpect(jsonPath("[*].field").exists());
    }

    @Test
    @DisplayName("댓글을 다려고 한 todo가 데이터베이스에 저장되어 있지 않은 todo일 때")
    void saveComment_empty_todo() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setBody("11시에 운동가기");

        mockMvc.perform(post(commentUri + "/{todoListId}", new Random().nextInt(1000))
                        .with(user(generateUserDetails_need_generateAccount()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").value("존재하지 않는 todo입니다."));
    }

    private UserDetails generateUserDetails() {
        return this.accountService.loadUserByUsername(appProperties.getTestEmail());
    }

    private UserDetails generateUserDetails_need_generateAccount() {
        generateAccount();
        return this.accountService.loadUserByUsername(appProperties.getTestEmail());
    }

    private Account generateAccount() {
        return this.accountRepo.save(
                Account.builder()
                        .email(appProperties.getTestEmail())
                        .password(encoder.encode(appProperties.getTestPassword()))
                        .createdAt(LocalDateTime.now())
                        .accountRoles(Collections.singletonList(AccountRole.USER))
                        .build()
        );
    }

    private TodoList generateTodo() {
        TodoList todoList = TodoList.builder()
                .description("아침 7시에 운동가기")
                .status(false)
                .createdAt(LocalDateTime.now())
                .account(generateAccount())
                .build();
        return tdlRepo.save(todoList);
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
}