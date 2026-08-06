package com.selcukaloba.to_do_api_project.repository;

import com.selcukaloba.to_do_api_project.dto.ApiUserResponse;
import com.selcukaloba.to_do_api_project.entity.FriendRequest;
import com.selcukaloba.to_do_api_project.entity.User;
import com.selcukaloba.to_do_api_project.enums.FriendRequestStatus;
import jakarta.persistence.Id;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
    List<FriendRequest> findByReceiverUsernameAndStatus(String username, FriendRequestStatus friendRequestStatus);
    boolean existsBySenderAndReceiverAndStatus(User sender, User receiver, FriendRequestStatus status);
    @Query("SELECT fr FROM FriendRequest fr WHERE " +
            "(fr.sender = :user1 AND fr.receiver = :user2) OR " +
            "(fr.sender = :user2 AND fr.receiver = :user1)")
    List<FriendRequest> findAllBySenderAndReceiverOrReverse(@Param("user1") User user1, @Param("user2") User user2);
}
