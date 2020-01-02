package me.mugon.todolist.todolist;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.mugon.todolist.domain.TodoList;
import me.mugon.todolist.domain.dto.TodoListDto;
import me.mugon.todolist.repository.TodoListRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class TodoListControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TodoListRepository tdlRepo;

    @Autowired
    private ModelMapper modelMapper;

    private final String todoUri = "/todolists";

    @Test
    @DisplayName("정상적으로 todo 생성하기")
    void create_todo() throws Exception {
        //GIVEN
        TodoListDto tdlDto = new TodoListDto();
        tdlDto.setDescription("자기소개서 쓰기");

        //WHEN, THEN
        mockMvc.perform(post(todoUri)
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
        TodoList savedTdl = createTdl();
        TodoListDto tdlDto = modelMapper.map(savedTdl, TodoListDto.class);
        tdlDto.setDescription("그냥 집에서 쉬기");

        mockMvc.perform(put(todoUri + "/{id}", savedTdl.getId())
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
        TodoList savedTdl = createTdl();
        TodoListDto tdlDto = modelMapper.map(savedTdl, TodoListDto.class);
        tdlDto.setDescription(description);

        mockMvc.perform(put(todoUri + "/{id}", savedTdl.getId())
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

        TodoList savedTdl = createTdl();
        TodoListDto tdlDto = modelMapper.map(savedTdl, TodoListDto.class);
        tdlDto.setDescription(longLengthString);

        mockMvc.perform(put(todoUri + "/{id}", savedTdl.getId())
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
        TodoList savedTdl = createTdl();

        mockMvc.perform(delete(todoUri + "/{id}", savedTdl.getId()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("삭제하려는 todo의 id가 저장되어 있지 않을 때")
    void delete_todo_not_found() throws Exception {
        createTdl();
        mockMvc.perform(delete(todoUri + "/{id}", new Random().nextInt(1000)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    private TodoList createTdl() {
        TodoList tdl = TodoList.builder()
                .description("운동 하기")
                .status(false)
                .createdAt(LocalDateTime.now())
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
}