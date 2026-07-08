package com.selcukaloba.to_do_api_project.dto;

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
public class TodoCreateRequest {

    @NotBlank(message = "title cannot be empty!")
    @Size(min =3, max =100, message = "title must be in 3-100 characters!")
    private String title;

    private String description;

    @NotNull(message = "task type should be given!")
    private TaskType taskType;

    @NotNull(message = "due time should be given!")
    @FutureOrPresent(message = "invalid date!")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dueDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "reminder date should be given!")
    @FutureOrPresent(message = "invalid date!")
    private LocalDateTime reminderDate;
}
//yeni eklenecek todonun idsi db'de yok, createdAt otomatik atanıyor