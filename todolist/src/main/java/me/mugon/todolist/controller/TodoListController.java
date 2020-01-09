package me.mugon.todolist.controller;

import lombok.RequiredArgsConstructor;
import me.mugon.todolist.annotation.SocialUser;
import me.mugon.todolist.annotation.CurrentUser;
import me.mugon.todolist.domain.Account;
import me.mugon.todolist.domain.dto.TodoListDto;
import me.mugon.todolist.service.TodoListService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping(value = "/todolists", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class TodoListController {

    private final TodoListService tdlService;

    @GetMapping
    public String getTodoLists(Model model, @CurrentUser Account currentUser) {
        model.addAttribute("tdl", tdlService.getTodoLists(currentUser));
        return "/tdl/list";
    }

    @GetMapping("/oauth")
    public String getTodoListsWithOAuth(Model model, @SocialUser Account currentUser) {
        model.addAttribute("tdl", tdlService.getTodoLists(currentUser));
        return "/tdl/list";
    }

    @PostMapping
    public ResponseEntity<?> saveTodoList(@RequestBody @Valid TodoListDto tdlDto, Errors errors, @CurrentUser Account currentUser) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors);
        }
        return tdlService.saveTodoList(tdlDto, currentUser);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<?> updateTodoList(@PathVariable Long id, @RequestBody @Valid TodoListDto tdlDto, Errors errors, @CurrentUser Account currentUser) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors);
        }
        return tdlService.updateTodoList(id, tdlDto);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteTodoList(@PathVariable Long id) {
        return tdlService.deleteTodoList(id);
    }
}
