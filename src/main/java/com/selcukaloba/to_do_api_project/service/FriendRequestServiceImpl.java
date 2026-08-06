package com.selcukaloba.to_do_api_project.service;

import com.selcukaloba.to_do_api_project.dto.ApiFriendRequestResponse;
import com.selcukaloba.to_do_api_project.dto.ApiUserResponse;
import com.selcukaloba.to_do_api_project.entity.FriendRequest;
import com.selcukaloba.to_do_api_project.entity.User;
import com.selcukaloba.to_do_api_project.enums.FriendRequestStatus;
import com.selcukaloba.to_do_api_project.exception.BaseException;
import com.selcukaloba.to_do_api_project.exception.ErrorMessage;
import com.selcukaloba.to_do_api_project.exception.MessageType;
import com.selcukaloba.to_do_api_project.repository.FriendRequestRepository;
import com.selcukaloba.to_do_api_project.repository.TodoShareRepository;
import com.selcukaloba.to_do_api_project.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FriendRequestServiceImpl implements IFriendRequestService{

    @Autowired
    private FriendRequestRepository friendRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TodoShareRepository todoShareRepository;

    @Override
    @Transactional
    public void sendFriendRequest(String senderUsername, String receiverUsername) {
        if(senderUsername.equals(receiverUsername))
        {
            throw new BaseException(new ErrorMessage(senderUsername, MessageType.CANNOT_SEND_TO_SELF));
        }

        User sender = userRepository.findByUsername(senderUsername).orElseThrow(()->new BaseException(new ErrorMessage(senderUsername, MessageType.USERNAME_NOT_FOUND)));
        User receiver = userRepository.findByUsername(receiverUsername).orElseThrow(()->new BaseException(new ErrorMessage(receiverUsername, MessageType.FRIEND_NOT_FOUND)));

        if(sender.getFriends().contains(receiver))
        {
            throw new BaseException(new ErrorMessage(receiverUsername, MessageType.ALREADY_FRIENDS));
        }

        boolean hasPendingRequests = friendRequestRepository.existsBySenderAndReceiverAndStatus(sender, receiver, FriendRequestStatus.PENDING);
        if(hasPendingRequests)
        {
            throw new BaseException(new ErrorMessage(receiverUsername, MessageType.FRIEND_REQUEST_ALREADY_PENDING));
        }
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setSender(sender);
        friendRequest.setReceiver(receiver);
        friendRequest.setStatus(FriendRequestStatus.PENDING);
        friendRequestRepository.save(friendRequest);
    }

    @Override
    public List<ApiFriendRequestResponse> getPendingRequests(String username) {
        List<FriendRequest> requests = friendRequestRepository.findByReceiverUsernameAndStatus(username, FriendRequestStatus.PENDING);
        return requests.stream()
                .map(req->new ApiFriendRequestResponse(
                        req.getId(),
                        req.getSender().getUsername(),
                        req.getReceiver().getUsername(),
                        req.getStatus()
                )).collect(Collectors.toList());

    }

    @Override
    @Transactional
    public void acceptRequest(Long requestId) {
        FriendRequest friendRequest = friendRequestRepository.findById(requestId).orElseThrow(()->new BaseException(new ErrorMessage(requestId.toString(), MessageType.FRIEND_REQUEST_NOT_FOUND)));
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
        FriendRequest friendRequest = friendRequestRepository.findById(requestId).orElseThrow(()-> new BaseException(new ErrorMessage(requestId.toString(), MessageType.FRIEND_REQUEST_NOT_FOUND)));
        friendRequestRepository.delete(friendRequest);
    }

    @Override
    public List<ApiUserResponse> getAllFriends(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(()->new BaseException(new ErrorMessage(username, MessageType.USERNAME_NOT_FOUND)));

        return user.getFriends().stream()
                .map(friend -> {
                    ApiUserResponse dto = new ApiUserResponse();
                    BeanUtils.copyProperties(friend, dto);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void unfriend(String currentUsername, String friendUsername, boolean keepShared)
    {
        User currentUser = userRepository.findByUsername(currentUsername).orElseThrow(()->new BaseException(new ErrorMessage(currentUsername, MessageType.USERNAME_NOT_FOUND)));
        User friendUser = userRepository.findByUsername(friendUsername).orElseThrow(()->new BaseException(new ErrorMessage(friendUsername, MessageType.USERNAME_NOT_FOUND)));

        currentUser.getFriends().remove(friendUser);
        friendUser.getFriends().remove(currentUser);
        userRepository.save(currentUser);
        userRepository.save(friendUser);

        List<FriendRequest> requests = friendRequestRepository.findAllBySenderAndReceiverOrReverse(currentUser, friendUser);
        friendRequestRepository.deleteAll(requests);

        if(!keepShared)
        {
            todoShareRepository.deleteAllByUsers(currentUser, friendUser);
        }
    }

}
