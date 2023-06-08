package com.baron.SocialMediaAPI.service;

import com.baron.SocialMediaAPI.model.FriendRequest;
import com.baron.SocialMediaAPI.model.User;
import com.baron.SocialMediaAPI.repository.FriendRequestRepository;
import com.baron.SocialMediaAPI.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class FriendshipService {

    private final UserRepository userRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final UserService userService;

    public void sendFriendRequest(User sender, User receiver) {
        receiver.getReceivedFriendRequests().add(new FriendRequest(sender, receiver));
        sender.getFollowing().add(receiver);
        userRepository.save(receiver);
    }

    public void acceptFriendRequest(FriendRequest request) {
        User receiver = request.getReceiver();
        receiver.getReceivedFriendRequests().remove(request);

        User sender = request.getSender();
        sender.getFriends().add(receiver);
        receiver.getFriends().add(sender);
        sender.getFollowers().add(receiver);

        friendRequestRepository.delete(request);
        userRepository.save(receiver);
    }

    public void removeFriend(User userToBeRemoved, User userWhoRemoves) {
        userWhoRemoves.getFriends().remove(userToBeRemoved);
        userWhoRemoves.getFollowing().remove(userToBeRemoved);
        userToBeRemoved.getFollowers().remove(userWhoRemoves);
        userToBeRemoved.getFriends().remove(userWhoRemoves);
        userRepository.save(userWhoRemoves);
    }

    public void removeRequest(String senderName, String receiverUsername) {
        FriendRequest friendRequest = friendRequestRepository
                .findBySenderUsernameAndReceiverUsername(senderName, receiverUsername);
        if (friendRequest == null) {
            throw new UsernameNotFoundException("User not found");
        }
        User receiver = (User)userService.loadUserByUsername(receiverUsername);
        receiver.getReceivedFriendRequests().remove(friendRequest);
        friendRequestRepository.delete(friendRequest);
    }

    public void addFriendsFromFollowers(User userWhoAdds, User userToBeAdded) {
        userWhoAdds.getFriends().add(userToBeAdded);
        userToBeAdded.getFriends().add(userWhoAdds);
        userWhoAdds.getFollowing().add(userToBeAdded);
        userRepository.save(userWhoAdds);
    }
}