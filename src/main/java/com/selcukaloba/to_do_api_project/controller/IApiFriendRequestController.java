package com.selcukaloba.to_do_api_project.controller;

import com.selcukaloba.to_do_api_project.dto.ApiFriendRequestResponse;

import java.security.Principal;
import java.util.List;

public interface IApiFriendRequestController {
    void sendFriendRequest(String receiverUsername, Principal principal);
    List<ApiFriendRequestResponse> getPendingRequests(Principal principal);
    void acceptRequest(Long requestId);
    void deleteRequest(Long requestId);
}
