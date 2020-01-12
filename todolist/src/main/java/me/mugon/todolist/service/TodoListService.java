package me.mugon.todolist.service;

import lombok.RequiredArgsConstructor;
import me.mugon.todolist.common.ErrorMessage;
import me.mugon.todolist.domain.Account;
import me.mugon.todolist.domain.TodoList;
import me.mugon.todolist.domain.dto.TodoListDto;
import me.mugon.todolist.repository.TodoListRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TodoListService {

    private final TodoListRepository tdlRepository;

    private final ModelMapper modelMapper;

    public List<TodoList> getTodoLists(Account currentUser) {
        return tdlRepository.findAllByAccount(currentUser);
    }

    public ResponseEntity<?> saveTodoList(TodoListDto tdlDto, Account currentUser) {
        TodoList tdl = this.modelMapper.map(tdlDto, TodoList.class);
        tdl.mappingAccount(currentUser);
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

    public ResponseEntity<?> deleteTodoList(Long id) {
        Optional<TodoList> todoListOptional = tdlRepository.findById(id);
        if (!todoListOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        TodoList tdl = todoListOptional.get();
        tdlRepository.delete(tdl);
        return ResponseEntity.ok().build();
    }

    public Optional<TodoList> findById(Long id) {
        return tdlRepository.findById(id);
    }

    public ResponseEntity<?> completeTodoList(Long id) {
        Optional<TodoList> byId = tdlRepository.findById(id);
        if (!byId.isPresent()) {
            return ResponseEntity.badRequest().body(new ErrorMessage("존재하지 않는 todo입니다."));
        }
        TodoList todoList = byId.get();
        todoList.updateStatusAndCompletedDate();
        TodoList statusTodoList = tdlRepository.save(todoList);
        return ResponseEntity.ok(statusTodoList);
    }
}
