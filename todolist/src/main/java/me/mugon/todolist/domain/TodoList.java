package me.mugon.todolist.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Getter @Setter @EqualsAndHashCode(of = "id")
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class TodoList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String description;

    @Column
    private boolean status;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @Column
    private LocalDateTime completedAt;

    @ManyToOne
    private Account account;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "todoList")
    private Set<Comment> comments;

    public void mappingAccount(Account currentUser) {
        if (this.account != null) {
            this.account.getTodoLists().remove(this);
        }
        this.account = currentUser;
        this.account.getTodoLists().add(this);
    }

    public void updateStatusAndCompletedDate() {
        this.status = !this.status;
        this.completedAt = this.status ? LocalDateTime.now() : null;
    }
}
