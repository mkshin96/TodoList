package me.mugon.todolist.repository;

import me.mugon.todolist.domain.Account;
import me.mugon.todolist.domain.TodoList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TodoListRepository extends JpaRepository<TodoList, Long> {
    List<TodoList> findAllByAccount(Account account);
}
