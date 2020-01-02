package me.mugon.todolist.controller;

import lombok.RequiredArgsConstructor;
import me.mugon.todolist.common.CurrentUser;
import me.mugon.todolist.domain.dto.AccountDto;
import me.mugon.todolist.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping(value = "/users")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<?> saveAccount(@Valid @RequestBody AccountDto accountDto, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors);
        }
        return accountService.saveAccount(accountDto);
    }
}
