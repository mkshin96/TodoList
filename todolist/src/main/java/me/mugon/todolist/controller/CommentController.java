package me.mugon.todolist.controller;

import lombok.RequiredArgsConstructor;
import me.mugon.todolist.annotation.CurrentUser;
import me.mugon.todolist.domain.Account;
import me.mugon.todolist.domain.dto.CommentDto;
import me.mugon.todolist.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{todoListId}")
    public ResponseEntity<?> saveComment(@PathVariable Long todoListId,
                                         @Valid @RequestBody CommentDto commentDto, Errors errors, @CurrentUser Account currentUser) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors);
        }
        return commentService.saveComment(todoListId, commentDto);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable Long commentId,
                                           @Valid @RequestBody CommentDto commentDto, Errors errors, @CurrentUser Account currentUser) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors);
        }
        return commentService.updateComment(commentId, commentDto);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId) {
        return commentService.deleteComment(commentId);
    }

}
