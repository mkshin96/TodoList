package me.mugon.todolist.service;

import lombok.RequiredArgsConstructor;
import me.mugon.todolist.domain.Account;
import me.mugon.todolist.domain.AccountRole;
import me.mugon.todolist.domain.adapter.AccountAdapter;
import me.mugon.todolist.domain.dto.AccountDto;
import me.mugon.todolist.repository.AccountRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;

    private final ModelMapper modelMapper;

    private final PasswordEncoder passwordEncoder;

    public ResponseEntity<?> saveAccount(AccountDto accountDto) {
        Optional<Account> accountOptional = accountRepository.findByEmail(accountDto.getEmail());
        if (accountOptional.isPresent()) {
            return ResponseEntity.badRequest().body("이미 존재하는 유저입니다.");
        }

        Account account = modelMapper.map(accountDto, Account.class);
        account.setAccountRoles(Collections.singletonList(AccountRole.USER));
        account.setCreatedAt(LocalDateTime.now());
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        Account newAccount = accountRepository.save(account);
        return new ResponseEntity<>(newAccount, HttpStatus.CREATED);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(username + "을 찾을 수 없습니다."));
        return new AccountAdapter(account);
    }
}
