package com.selcukaloba.to_do_api_project.service;

import com.selcukaloba.to_do_api_project.entity.FriendRequest;
import com.selcukaloba.to_do_api_project.entity.User;
import com.selcukaloba.to_do_api_project.enums.FriendRequestStatus;
import com.selcukaloba.to_do_api_project.repository.FriendRequestRepository;
import com.selcukaloba.to_do_api_project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FriendRequestServiceImpl implements IFriendRequestService{

    @Autowired
    private FriendRequestRepository friendRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void sendFriendRequest(String senderUsername, String receiverUsername) {
        if(senderUsername.equals(receiverUsername))
        {
            throw new RuntimeException("You cannot send a request to yourself!");
        }

        User sender = userRepository.findByUsername(senderUsername).orElseThrow(()->new RuntimeException("Sender could not found!"));
        User receiver = userRepository.findByUsername(receiverUsername).orElseThrow(()->new RuntimeException("Receiver could not found!"));

        if(sender.getFriends().contains(receiver))
        {
            throw new RuntimeException("You are already friends!");
        }
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setSender(sender);
        friendRequest.setReceiver(receiver);
        friendRequest.setStatus(FriendRequestStatus.PENDING);
        friendRequestRepository.save(friendRequest);
    }

    @Override
    public List<FriendRequest> getPendingRequests(String username) {
        return friendRequestRepository.findByReceiverUsernameAndStatus(username, FriendRequestStatus.PENDING);
    }

    @Override
    public void acceptRequest(Long requestId) {
        FriendRequest friendRequest = friendRequestRepository.findById(requestId).orElseThrow(()->new RuntimeException("Invalid request id!"));
        User sender = friendRequest.getSender();
        User receiver = friendRequest.getReceiver();
        sender.getFriends().add(receiver);
        receiver.getFriends().add(sender);
        userRepository.save(sender);
        userRepository.save(receiver);
        friendRequestRepository.delete(friendRequest);
    }

    @Override
    public void deleteRequest(Long requestId) {
        FriendRequest friendRequest = friendRequestRepository.findById(requestId).orElseThrow(()-> new RuntimeException("Invalid request id!"));
        friendRequestRepository.delete(friendRequest);
    }
}
