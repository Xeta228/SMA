package com.baron.SocialMediaAPI.repository;

import com.baron.SocialMediaAPI.model.FriendRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
    FriendRequest findBySenderUsernameAndReceiverUsername(String senderUsername, String receiverUsername);
}
