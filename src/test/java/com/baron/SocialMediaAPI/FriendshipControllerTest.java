package com.baron.SocialMediaAPI;

import com.baron.SocialMediaAPI.controller.FriendshipController;
import com.baron.SocialMediaAPI.dto.FriendRequestDto;
import com.baron.SocialMediaAPI.dto.UserDto;
import com.baron.SocialMediaAPI.model.FriendRequest;
import com.baron.SocialMediaAPI.model.User;
import com.baron.SocialMediaAPI.service.FriendshipService;
import com.baron.SocialMediaAPI.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FriendshipControllerTest {

    private FriendshipController friendshipController;

    @Mock
    private UserService userService;

    @Mock
    private FriendshipService friendshipService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        friendshipController = new FriendshipController(userService, friendshipService);
    }

    @Test
    void sendFriendRequest_withValidReceiverName_shouldSendRequest() {
        String receiverName = "receiver";
        User currentUser = new User();
        currentUser.setUsername("sender");
        currentUser.setFriends(new ArrayList<>());

        User receiver = new User();
        receiver.setUsername(receiverName);
        receiver.setFriends(new ArrayList<>());

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(userService.loadUserByUsername(receiverName)).thenReturn(receiver);

        ResponseEntity<String> response = friendshipController.sendFriendRequest(receiverName);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Friend request successfully sent", response.getBody());

        verify(friendshipService).sendFriendRequest(currentUser, receiver);
    }

    @Test
    void sendFriendRequest_withInvalidReceiverName_shouldReturnNotFound() {
        String receiverName = "receiver";
        User currentUser = new User();
        currentUser.setUsername("sender");
        currentUser.setReceivedFriendRequests(new ArrayList<>());
        currentUser.setFriends(new ArrayList<>());

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(userService.loadUserByUsername(receiverName)).thenThrow(new UsernameNotFoundException("User not found"));

        assertThrows(UsernameNotFoundException.class, () -> {
            friendshipController.sendFriendRequest(receiverName);
        });

        verify(friendshipService, never()).sendFriendRequest(any(), any());
    }

    @Test
    void sendFriendRequest_withSameSenderAndReceiver_shouldReturnUnavailableForLegalReasons() {
        // Arrange
        String receiverName = "sender";
        User currentUser = new User();
        currentUser.setUsername(receiverName);

        when(userService.getCurrentUser()).thenReturn(currentUser);

        // Act
        ResponseEntity<String> response = friendshipController.sendFriendRequest(receiverName);

        // Assert
        assertEquals(HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS, response.getStatusCode());
        assertEquals("Cannot send request to yourself", response.getBody());

        verify(friendshipService, never()).sendFriendRequest(currentUser, currentUser);
    }

    @Test
    void sendFriendRequest_withAlreadyFriends_shouldReturnNotFound() {
        String receiverName = "receiver";
        User currentUser = new User();
        currentUser.setUsername("sender");
        currentUser.setFriends(new ArrayList<>());

        User receiver = new User();
        receiver.setUsername(receiverName);
        currentUser.getFriends().add(receiver);

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(userService.loadUserByUsername(receiverName)).thenReturn(receiver);

        ResponseEntity<String> response = friendshipController.sendFriendRequest(receiverName);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(null, response.getBody());

        verify(friendshipService, never()).sendFriendRequest(currentUser, receiver);
    }

    @Test
    void showAllFriends_shouldReturnListOfFriends() {
        User currentUser = new User();
        currentUser.setUsername("sender");
        currentUser.setFriends(new ArrayList<>());
        User friend1 = new User();
        friend1.setUsername("friend1");

        User friend2 = new User();
        friend2.setUsername("friend2");

        currentUser.getFriends().add(friend1);
        currentUser.getFriends().add(friend2);

        when(userService.getCurrentUser()).thenReturn(currentUser);

        List<UserDto> response = friendshipController.showAllFriends();

        assertEquals(2, response.size());
        assertEquals("friend1", response.get(0).getUsername());
        assertEquals("friend2", response.get(1).getUsername());

        verifyNoInteractions(friendshipService);
    }

    @Test
    void showAllFriendRequests_shouldReturnListOfFriendRequests() {
        User currentUser = new User();
        currentUser.setUsername("receiver");
        currentUser.setReceivedFriendRequests(new ArrayList<>()); // Initialize the receivedFriendRequests list

        User sender1 = new User();
        sender1.setUsername("sender1");

        User sender2 = new User();
        sender2.setUsername("sender2");

        FriendRequest friendRequest1 = new FriendRequest();
        friendRequest1.setReceiver(currentUser);
        friendRequest1.setSender(sender1);

        FriendRequest friendRequest2 = new FriendRequest();
        friendRequest2.setReceiver(currentUser);
        friendRequest2.setSender(sender2);

        currentUser.getReceivedFriendRequests().add(friendRequest1);
        currentUser.getReceivedFriendRequests().add(friendRequest2);

        when(userService.getCurrentUser()).thenReturn(currentUser);

        List<FriendRequestDto> response = friendshipController.showAllFriendRequests();

        assertEquals(2, response.size());
        assertEquals("sender1", response.get(0).getSenderName());
        assertEquals("sender2", response.get(1).getSenderName());

        verifyNoInteractions(friendshipService);
    }

    @Test
    void acceptFriendRequest_withValidSenderName_shouldAcceptRequest() {
        String receiverName = "receiver";
        User currentUser = new User();
        currentUser.setUsername("sender");
        currentUser.setFriends(new ArrayList<>());

        User receiver = new User();
        receiver.setUsername(receiverName);

        currentUser.getFriends().add(receiver);

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(userService.loadUserByUsername(receiverName)).thenReturn(receiver);

        ResponseEntity<String> response = friendshipController.sendFriendRequest(receiverName);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

        verify(friendshipService, never()).sendFriendRequest(currentUser, receiver);
    }

    @Test
    void acceptFriendRequest_withInvalidSenderName_shouldReturnNotFound() {
        String senderName = "sender";
        User currentUser = new User();
        currentUser.setUsername("receiver");
        currentUser.setFriends(new ArrayList<>());
        currentUser.setReceivedFriendRequests(new ArrayList<>());

        User sender = new User();
        sender.setUsername(senderName);
        sender.setFriends(new ArrayList<>());

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(userService.loadUserByUsername(senderName)).thenReturn(sender);

        ResponseEntity<String> response = friendshipController.acceptFriendRequest(senderName);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Request not found.", response.getBody());

        verify(friendshipService, never()).acceptFriendRequest(any());
    }

    @Test
    void acceptFriendRequest_withAlreadyFriends_shouldReturnNotFound() {

        String senderName = "sender";
        User currentUser = new User();
        currentUser.setUsername("receiver");
        currentUser.setFriends(new ArrayList<>());
        currentUser.setReceivedFriendRequests(new ArrayList<>());

        User sender = new User();
        sender.setUsername(senderName);
        sender.setFriends(new ArrayList<>());
        currentUser.getFriends().add(sender);

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(userService.loadUserByUsername(senderName)).thenReturn(sender);

        ResponseEntity<String> response = friendshipController.acceptFriendRequest(senderName);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Request not found.", response.getBody());

        verify(friendshipService, never()).acceptFriendRequest(any());
    }

    @Test
    void removeFriend_withValidSenderName_shouldRemoveFriendship() {
        String senderName = "friend1";
        User currentUser = new User();
        currentUser.setUsername("friend2");
        currentUser.setFriends(new ArrayList<>());

        User sender = new User();
        sender.setUsername(senderName);
        sender.setFriends(new ArrayList<>());

        currentUser.getFriends().add(sender);
        sender.getFriends().add(currentUser);

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(userService.loadUserByUsername(senderName)).thenReturn(sender);

        ResponseEntity<String> response = friendshipController.removeFriend(senderName);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Friendship has been successfully removed", response.getBody());

        verify(friendshipService).removeFriend(sender, currentUser);
    }

    @Test
    void removeFriend_withInvalidSenderName_shouldReturnNotFound() {
        String senderName = "friend1";
        User currentUser = new User();
        currentUser.setUsername("friend2");

        User sender = new User();
        sender.setUsername(senderName);

        currentUser.setFriends(new ArrayList<>());
        currentUser.getFriends().add(sender);
        sender.setFriends(new ArrayList<>());
        sender.getFriends().add(currentUser);

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(userService.loadUserByUsername(senderName)).thenReturn(sender);

        ResponseEntity<String> response = friendshipController.removeFriend(senderName);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Friendship has been successfully removed", response.getBody());

        verify(friendshipService).removeFriend(sender, currentUser);
    }

    @Test
    void removeFriend_withNonFriendSender_shouldReturnNotFound() {
        String senderName = "friend1";
        User currentUser = new User();
        currentUser.setUsername("friend2");
        currentUser.setFriends(new ArrayList<>());
        User sender = new User();
        sender.setUsername(senderName);
        sender.setFriends(new ArrayList<>());
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(userService.loadUserByUsername(senderName)).thenReturn(sender);

        ResponseEntity<String> response = friendshipController.removeFriend(senderName);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("No such friend found", response.getBody());

        verify(friendshipService, never()).removeFriend(any(), any());
    }

    @Test
    void getAllFollowers_shouldReturnListOfFollowers() {
        User currentUser = new User();
        currentUser.setUsername("receiver");
        currentUser.setFollowers(new ArrayList<>());  // Initialize followers list

        User follower1 = new User();
        follower1.setUsername("follower1");

        User follower2 = new User();
        follower2.setUsername("follower2");

        currentUser.getFollowers().add(follower1);
        currentUser.getFollowers().add(follower2);

        when(userService.getCurrentUser()).thenReturn(currentUser);

        List<UserDto> response = friendshipController.getAllFollowers();

        assertEquals(2, response.size());
        assertEquals("follower1", response.get(0).getUsername());
        assertEquals("follower2", response.get(1).getUsername());

        verifyNoInteractions(friendshipService);
    }

    @Test
    void getAllFollowing_shouldReturnListOfFollowing() {
        User currentUser = new User();
        currentUser.setUsername("sender");
        currentUser.setFollowing(new ArrayList<>());  // Initialize following list

        User following1 = new User();
        following1.setUsername("following1");

        User following2 = new User();
        following2.setUsername("following2");

        currentUser.getFollowing().add(following1);
        currentUser.getFollowing().add(following2);

        when(userService.getCurrentUser()).thenReturn(currentUser);

        List<UserDto> response = friendshipController.getAllFollowing();

        assertEquals(2, response.size());
        assertEquals("following1", response.get(0).getUsername());
        assertEquals("following2", response.get(1).getUsername());

        verifyNoInteractions(friendshipService);
    }
}
