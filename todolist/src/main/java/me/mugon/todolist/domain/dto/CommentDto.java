package me.mugon.todolist.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class CommentDto {

    @NotBlank(message = "댓글은 1자 이상 255자 미만이어야 합니다.")
    @Size(min = 1, max = 255, message = "댓글은 1자 이상 255자 미만이어야 합니다.")
    private String body;
}
