package me.mugon.todolist.common;

import lombok.RequiredArgsConstructor;
import me.mugon.todolist.domain.Account;
import me.mugon.todolist.domain.TodoList;
import me.mugon.todolist.domain.enums.AccountRole;
import me.mugon.todolist.repository.AccountRepository;
import me.mugon.todolist.repository.TodoListRepository;
import me.mugon.todolist.service.AccountService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class DummyData implements ApplicationRunner {

    private final AccountRepository accountRepo;

    private final TodoListRepository tdlRepo;

    private final AppProperties appProperties;

    private final PasswordEncoder encoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Account account = Account.builder()
                                    .email(appProperties.getTestEmail())
                                    .password(encoder.encode(appProperties.getTestPassword()))
                                    .createdAt(LocalDateTime.now())
                                    .accountRoles(Arrays.asList(AccountRole.USER))
                                    .build();
        accountRepo.save(account);

        IntStream.rangeClosed(1,20)
                .forEach((index) -> {
                    TodoList todoList = TodoList.builder()
                            .description("할 일" + index)
                            .status(false)
                            .createdAt(LocalDateTime.now())
                            .account(account)
                            .build();
                    tdlRepo.save(todoList);
                });
    }
}
