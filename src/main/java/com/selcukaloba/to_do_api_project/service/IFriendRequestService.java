package com.selcukaloba.to_do_api_project.service;

import com.selcukaloba.to_do_api_project.dto.ApiFriendRequestResponse;
import com.selcukaloba.to_do_api_project.dto.ApiUserResponse;

import java.util.List;

public interface IFriendRequestService {
    void sendFriendRequest(String senderUsername, String receiverUsername);
    List<ApiFriendRequestResponse> getPendingRequests(String username);
    void acceptRequest(Long requestId);
    void deleteRequest(Long requestId);
    List<ApiUserResponse> getAllFriends(String username);
    void unfriend(String currentUsername, String friendUsername, boolean keepShared);
}
