package com.selcukaloba.to_do_api_project.controller;

import com.selcukaloba.to_do_api_project.dto.ApiFriendRequestResponse;
import com.selcukaloba.to_do_api_project.service.IFriendRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/friends")
public class ApiFriendRequestControllerImpl implements IApiFriendRequestController {

    @Autowired
    private IFriendRequestService friendRequestService;

    @Override
    @PostMapping(path = "/request/send")
    public void sendFriendRequest(@RequestParam String receiverUsername, Principal principal) {
        String loginuser = principal.getName();
        friendRequestService.sendFriendRequest(loginuser, receiverUsername);
    }

    @Override
    @GetMapping(path = "/request/pending")
    public List<ApiFriendRequestResponse> getPendingRequests(Principal principal) {
        String loginuser = principal.getName();
        return friendRequestService.getPendingRequests(loginuser);
    }

    @Override
    @PostMapping(path = "/request/accept/{requestId}")
    public void acceptRequest(@PathVariable Long requestId) {
        friendRequestService.acceptRequest(requestId);
    }

    @Override
    @DeleteMapping(path = "/request/delete/{requestId}")
    public void deleteRequest(@PathVariable Long requestId) {
        friendRequestService.deleteRequest(requestId);
    }
}
