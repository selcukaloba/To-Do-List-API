package com.selcukaloba.to_do_api_project.dto.todo;

import com.selcukaloba.to_do_api_project.enums.TaskType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TodoResponse {

    private Long id;
    private String title;
    private String description;
    private TaskType taskType;
    private LocalDateTime createdAt;
    private LocalDateTime dueDate;
    private LocalDateTime reminderDate;
    private boolean isCompleted;
}
//frontende dönülmesi gereken tüm veriler
