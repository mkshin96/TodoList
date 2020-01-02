package me.mugon.todolist.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class TodoListDto {

    @NotBlank(message = "todo는 공백일 수 없습니다.")
    @Size(min = 1, max = 255, message = "todo의 길이는 1자이상, 255자 미만이어야 합니다.")
    private String description;

    private boolean status;
}
