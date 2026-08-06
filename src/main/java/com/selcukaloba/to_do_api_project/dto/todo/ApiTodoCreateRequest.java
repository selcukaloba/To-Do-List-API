package com.selcukaloba.to_do_api_project.dto.todo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.selcukaloba.to_do_api_project.enums.TaskType;
import com.selcukaloba.to_do_api_project.validator.TaskDateMatch;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TaskDateMatch
public class ApiTodoCreateRequest {

    @NotBlank(message = "{todo.title.not_blank}")
    @Size(min =3, max =100, message = "{todo.title.size}")
    private String title;

    private String description;

    @NotNull(message = "{todo.task_type.not_null}")
    private TaskType taskType;

    @NotNull(message = "{todo.due.date.not_null}")
    @FutureOrPresent(message = "{todo.task.invalid_date}")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dueDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "{todo.reminder.date.not_null}")
    @FutureOrPresent(message = "{todo.task.invalid_date}")
    private LocalDateTime reminderDate;

    private Long teamId;
    private String assignedToUsername;
}
