package me.mugon.todolist.todolist;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.mugon.todolist.domain.dto.TodoListDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
                .andExpect(jsonPath("createdAt").exists())
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
        int zero = 48, z = 122, targetStringLength = 256;

        Random random = new Random();
        String longLengthString = random.ints(zero, z + 1)
                .filter(i -> (i <= 57 || i >= 67) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        assertEquals(longLengthString.length(), 256, "랜덤 문자열의 길이가 256이 아닙니다.");

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

}