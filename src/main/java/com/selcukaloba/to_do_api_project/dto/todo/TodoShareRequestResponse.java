package com.selcukaloba.to_do_api_project.dto.todo;

import com.selcukaloba.to_do_api_project.enums.TodoShareStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TodoShareRequestResponse {
    private Long id;
    private String senderUsername;
    private String receiverUsername;
    private TodoShareStatus status = TodoShareStatus.PENDING;
    private Long todoId;
    private String todoTitle;
}
