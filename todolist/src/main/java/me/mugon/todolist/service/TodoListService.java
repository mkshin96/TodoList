package me.mugon.todolist.service;

import lombok.RequiredArgsConstructor;
import me.mugon.todolist.domain.TodoList;
import me.mugon.todolist.domain.dto.TodoListDto;
import me.mugon.todolist.repository.TodoListRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TodoListService {

    private final TodoListRepository tdlRepository;

    private final ModelMapper modelMapper;

    public ResponseEntity<?> saveTodoList(TodoListDto tdlDto) {
        TodoList tdl = this.modelMapper.map(tdlDto, TodoList.class);
        TodoList newTdl = this.tdlRepository.save(setCreateAtAndStatus(tdl));
        return new ResponseEntity<>(newTdl, HttpStatus.CREATED);
    }

    private TodoList setCreateAtAndStatus(TodoList tdl) {
        tdl.setCreatedAt(LocalDateTime.now());
        tdl.setStatus(false);
        return tdl;
    }

    public ResponseEntity<?> updateTodoList(Long id, TodoListDto tdlDto) {
        Optional<TodoList> updatedTdl = tdlRepository.findById(id);
        if (!updatedTdl.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        TodoList todoList = updatedTdl.get();
        this.modelMapper.map(tdlDto, todoList);
        todoList.setUpdatedAt(LocalDateTime.now());
        TodoList savedTdl = tdlRepository.save(todoList);
        return ResponseEntity.ok().body(savedTdl);
    }
}
