package me.mugon.todolist.service;

import lombok.RequiredArgsConstructor;
import me.mugon.todolist.common.ErrorMessage;
import me.mugon.todolist.domain.Comment;
import me.mugon.todolist.domain.TodoList;
import me.mugon.todolist.domain.dto.CommentDto;
import me.mugon.todolist.repository.CommentRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    private final ModelMapper modelMapper;

    private final TodoListService todoListService;

    public ResponseEntity<?> saveComment(Long todoListId, CommentDto commentDto) {
        Comment comment = modelMapper.map(commentDto, Comment.class);
        comment.setCreatedAt(LocalDateTime.now());

        Optional<TodoList> todoList = todoListService.findById(todoListId);
        if (!todoList.isPresent()) {
            return new ResponseEntity<>(new ErrorMessage("존재하지 않는 todo입니다."), HttpStatus.BAD_REQUEST);
        }
        Comment newComment = commentRepository.save(comment.mappingTodo(todoList.get()));
        return new ResponseEntity<>(newComment, HttpStatus.CREATED);
    }

    public ResponseEntity<?> updateComment(Long commentId, CommentDto commentDto) {
        Optional<Comment> comment = commentRepository.findById(commentId);
        if (!comment.isPresent()) {
            return new ResponseEntity<>(new ErrorMessage("존재하지 않는 댓글입니다."), HttpStatus.BAD_REQUEST);
        }
        Comment updatingComment = comment.get();
        modelMapper.map(commentDto, updatingComment);
        updatingComment.setUpdatedAt(LocalDateTime.now());
        Comment updatedComment = commentRepository.save(updatingComment);
        return new ResponseEntity<>(updatedComment, HttpStatus.OK);
    }

    public ResponseEntity<?> deleteComment(Long commentId) {
        Optional<Comment> comment = commentRepository.findById(commentId);
        if (!comment.isPresent()) {
            return new ResponseEntity<>(new ErrorMessage("존재하지 않는 댓글입니다."), HttpStatus.BAD_REQUEST);
        }
        commentRepository.delete(comment.get());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
