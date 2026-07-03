package com.selcukaloba.to_do_api_project.Entity;

import com.selcukaloba.to_do_api_project.Enums.TaskType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "todo")
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "title", length = 100)
    private String title;

    @Column(name = "description", length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "task_type")
    private TaskType taskType;

    @CreationTimestamp
    @Column(nullable = false, name = "created_at")
    private LocalDateTime createdAt;

    @Column(nullable = false, name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "reminder_date")
    private LocalDateTime reminderDate;

    @Column(nullable = false, name = "is_completed")
    private boolean isCompleted = false;
}
