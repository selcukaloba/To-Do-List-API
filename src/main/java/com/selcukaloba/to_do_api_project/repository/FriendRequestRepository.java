package com.selcukaloba.to_do_api_project.repository;

import com.selcukaloba.to_do_api_project.entity.FriendRequest;
import com.selcukaloba.to_do_api_project.enums.FriendRequestStatus;
import jakarta.persistence.Id;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
    List<FriendRequest> findByReceiverUsernameAndStatus(String username, FriendRequestStatus friendRequestStatus);
}
