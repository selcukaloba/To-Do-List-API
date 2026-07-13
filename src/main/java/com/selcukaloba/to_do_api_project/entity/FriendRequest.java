package com.selcukaloba.to_do_api_project.entity;

import com.selcukaloba.to_do_api_project.enums.FriendRequestStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "friend_requests")
public class FriendRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;
    @ManyToOne
    @JoinColumn(name = "receiver", nullable = false)
    private User receiver;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private FriendRequestStatus status = FriendRequestStatus.PENDING;
}
