package me.mugon.todolist.controller;

import lombok.RequiredArgsConstructor;
import me.mugon.todolist.domain.dto.TodoListDto;
import me.mugon.todolist.service.TodoListService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping(value = "/todolists", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class TodoListController {

    private final TodoListService tdlService;

    @PostMapping
    public ResponseEntity<?> saveTodoList(@RequestBody @Valid TodoListDto tdlDto, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors);
        }
        return tdlService.saveTodoList(tdlDto);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<?> updateTodoList(@PathVariable Long id, @RequestBody @Valid TodoListDto tdlDto, Errors errors) {
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
