package com.baron.SocialMediaAPI.controller;


import com.baron.SocialMediaAPI.dto.FriendRequestDto;
import com.baron.SocialMediaAPI.dto.UserDto;
import com.baron.SocialMediaAPI.model.FriendRequest;
import com.baron.SocialMediaAPI.model.User;
import com.baron.SocialMediaAPI.service.FriendshipService;
import com.baron.SocialMediaAPI.service.UserService;
import com.baron.SocialMediaAPI.util.FriendRequestUtil;
import com.baron.SocialMediaAPI.util.UserUtil;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friend")
@RequiredArgsConstructor
public class FriendshipController {


    private final UserService userService;
    private final FriendshipService friendshipService;

    @PostMapping("request/{receiverName}")
    @Operation(summary = "Send friend request to user with specified username")
    public ResponseEntity<String> sendFriendRequest(@PathVariable String receiverName) {
        User curr = userService.getCurrentUser();
        User receiver = (User)userService.loadUserByUsername(receiverName);


        if (curr.getUsername().equals(receiverName)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Cannot send request to yourself");
        }

        if (curr.getFriends().contains(receiver)) {
            return ResponseEntity.notFound().build();
        }

        friendshipService.sendFriendRequest(curr, receiver);
        return ResponseEntity.ok("Friend request successfully sent");
    }

    @GetMapping("/all")
    @Operation(summary = "Show all friends of currently logged in user")
    public List<UserDto> showAllFriends() {
        User curr = userService.getCurrentUser();
        return curr.getFriends().stream().map(UserUtil::mapUserToUserDto).toList();
    }

    @GetMapping("/request")
    @Operation(summary = "Show all friend requests of currently logged in user")
    public List<FriendRequestDto> showAllFriendRequests() {
        User curr = userService.getCurrentUser();
        return curr.getReceivedFriendRequests().stream().map(FriendRequestUtil::mapFriendRequestToFriendRequestDto)
                .toList();
    }

    @PutMapping("request/{senderName}/accept")
    @Operation(summary = "Accept friend request from a specified user")
    public ResponseEntity<String> acceptFriendRequest(@PathVariable String senderName) {
        User curr = userService.getCurrentUser();
        User sender = (User)userService.loadUserByUsername(senderName);
        FriendRequest request = new FriendRequest(sender, curr);

        if (sender.getFriends().contains(curr) || !curr.getReceivedFriendRequests().contains(request)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Request not found.");
        }

        friendshipService.acceptFriendRequest(request);
        return ResponseEntity.ok("Friendship request accepted");
    }

    @DeleteMapping("/{senderName}")
    @Operation(summary = "Remove user with specified username from the friendlist")
    public ResponseEntity<String> removeFriend(@PathVariable String senderName) {
        User curr = userService.getCurrentUser();
        User sender = (User)userService.loadUserByUsername(senderName);

        if (!sender.getFriends().contains(curr) || curr.equals(sender)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such friend found");
        }

        friendshipService.removeFriend(sender, curr);
        return ResponseEntity.ok("Friendship has been successfully removed");
    }

    @GetMapping("/followers")
    @Operation(summary = "View all followers for currently logged in user")
    public List<UserDto> getAllFollowers() {
        User curr = userService.getCurrentUser();
        return curr.getFollowers().stream().map(UserUtil::mapUserToUserDto).toList();
    }

    @GetMapping("/following")
    @Operation(summary = "View all users the currently logged in user is following")
    public List<UserDto> getAllFollowing() {
        User curr = userService.getCurrentUser();
        return curr.getFollowing().stream().map(UserUtil::mapUserToUserDto).toList();
    }

    @PutMapping("/followers/{senderName}")
    @Operation(summary = "Add user with specified username from followers into the friend list")
    public ResponseEntity<String> addFriendFromFollowers(@PathVariable String senderName) {
        User curr = userService.getCurrentUser();
        User userToBeAdded = (User)userService.loadUserByUsername(senderName);

        if (!curr.getFollowers().contains(userToBeAdded)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User is not in your follower list");
        }
        if (curr.getFriends().contains(userToBeAdded)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User is already in your friend list!");
        }

        friendshipService.addFriendsFromFollowers(curr, userToBeAdded);
        return ResponseEntity.ok("User successfully added");
    }

    @DeleteMapping("/request/{senderName}")
    @Operation(summary = "Reject friendship request. User will remain as follower")
    public void rejectFriendShipRequest(@PathVariable String senderName) {
        User curr = userService.getCurrentUser();
        friendshipService.removeRequest(senderName, curr.getUsername());
    }

}
