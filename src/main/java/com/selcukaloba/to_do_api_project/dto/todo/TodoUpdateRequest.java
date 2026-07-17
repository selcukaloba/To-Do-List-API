package com.selcukaloba.to_do_api_project.dto.todo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.selcukaloba.to_do_api_project.enums.TaskType;
import com.selcukaloba.to_do_api_project.validator.TaskDateMatch;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TaskDateMatch
public class TodoUpdateRequest {

    @NotBlank(message = "title cannot be empty!")
    @Size(min =3, max =100, message = "title must be in 3-100 characters!")
    private String title;

    private String description;

    @NotNull(message = "task type should be given!")
    private TaskType taskType;

    @NotNull(message = "due time should be given!")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dueDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "reminder date should be given!")
    private LocalDateTime reminderDate;

    private boolean isCompleted;
}
//create requestten farkı: update ederken isCompleted değiştirilebilir, onda direkt false olarak üretiliyor
