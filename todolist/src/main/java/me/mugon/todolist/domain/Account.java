package me.mugon.todolist.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import me.mugon.todolist.domain.enums.AccountRole;
import me.mugon.todolist.domain.enums.SocialType;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@NoArgsConstructor @AllArgsConstructor @Builder @ToString
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    @Column
    @JsonIgnore
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private List<AccountRole> accountRoles;

    @Column
    private String principal; //oauth2인증으로 제공받는 키 값

    @Column
    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    @Column
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "account", fetch = FetchType.EAGER)
    private Set<TodoList> todoLists = new HashSet<>();
}
