package com.selcukaloba.to_do_api_project.controller;

import com.selcukaloba.to_do_api_project.dto.FriendRequestResponse;
import com.selcukaloba.to_do_api_project.entity.FriendRequest;

import java.security.Principal;
import java.util.List;

public interface IFriendRequestController {
    void sendFriendRequest(String receiverUsername, Principal principal);
    List<FriendRequestResponse> getPendingRequests(Principal principal);
    void acceptRequest(Long requestId);
    void deleteRequest(Long requestId);
}
