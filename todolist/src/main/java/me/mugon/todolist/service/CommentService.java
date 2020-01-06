package me.mugon.todolist.service;

import lombok.RequiredArgsConstructor;
import me.mugon.todolist.domain.Comment;
import me.mugon.todolist.domain.dto.CommentDto;
import me.mugon.todolist.repository.CommentRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    private final ModelMapper modelMapper;

    private final TodoListService todoListService;

    public ResponseEntity<?> saveComment(Long todoListId, CommentDto commentDto) {
        Comment comment = modelMapper.map(commentDto, Comment.class);
        comment.setCreatedAt(LocalDateTime.now());
        Comment newComment = commentRepository.save(comment.mappingTodo(todoListService.findById(todoListId)));
        return new ResponseEntity<>(newComment, HttpStatus.CREATED);
    }
}
