package com.selcukaloba.to_do_api_project.dto;

import com.selcukaloba.to_do_api_project.enums.FriendRequestStatus;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendRequestResponse {
    private Long id;
    private String senderUsername;
    private String receiverUsername;
    private FriendRequestStatus status = FriendRequestStatus.PENDING;
}
