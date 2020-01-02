package me.mugon.todolist.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AccountDto {

    @NotBlank
    private String email;

    @NotBlank
    private String password;

}
