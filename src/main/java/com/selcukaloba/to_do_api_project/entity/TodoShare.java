package com.selcukaloba.to_do_api_project.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "todo_shared_users")
public class TodoShare {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "todo_id", nullable = false)
    private Todo todo;
    @ManyToOne
    @JoinColumn(name = "shared_user_id", nullable = false)
    private User sharedUser;
    @ManyToOne
    @JoinColumn(name = "shared_by_user_id", nullable = false)
    private User sharedBy;
}
