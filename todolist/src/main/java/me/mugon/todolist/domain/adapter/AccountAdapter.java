package me.mugon.todolist.domain.adapter;

import me.mugon.todolist.domain.Account;
import me.mugon.todolist.domain.enums.AccountRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.*;

public class AccountAdapter extends User {

    private Account account;

    public AccountAdapter(Account account) {
        super(account.getEmail(), account.getPassword(), authorities(account.getAccountRoles()));
        this.account = account;
    }

    private static Collection<? extends GrantedAuthority> authorities(List<AccountRole> accountRoles) {
        return accountRoles.stream()
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                            .collect(toList());
    }

    public Account getAccount() {
        return account;
    }


}
