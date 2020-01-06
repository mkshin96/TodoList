package me.mugon.todolist.domain.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class AccountDto {

    @NotBlank
    @Email(message = "이메일 형식이 맞지 않습니다.")
    private String email;

    @NotBlank
    @Size(min = 8, max = 25, message = "비밀번호는 8자 이상 25자 미만입니다.")
    private String password;

}
