package com.baron.SocialMediaAPI;

import com.baron.SocialMediaAPI.controller.MessageController;
import com.baron.SocialMediaAPI.dto.MessageDto;
import com.baron.SocialMediaAPI.dto.MessageSendRequest;
import com.baron.SocialMediaAPI.model.User;
import com.baron.SocialMediaAPI.service.MessageService;
import com.baron.SocialMediaAPI.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class MessageControllerTest {

    @Mock
    private MessageService messageService;

    @Mock
    private UserService userService;

    private MessageController messageController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        messageController = new MessageController(messageService, userService);
    }

    @Test
    void retrieveAllInboxMessages_shouldReturnListOfMessages() {

        User currentUser = new User();
        currentUser.setUsername("testuser");

        List<MessageDto> expectedMessages = new ArrayList<>();
        expectedMessages.add(new MessageDto("Receiver 1", "Message 1"));
        expectedMessages.add(new MessageDto("Receiver 2", "Message 2"));

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(messageService.getAllInboxMessagesForUser(currentUser.getUsername())).thenReturn(expectedMessages);


        List<MessageDto> actualMessages = messageController.retrieveAllInboxMessages();


        assertEquals(expectedMessages, actualMessages);
    }

    @Test
    void retrieveAllMessages_shouldReturnListOfMessages() {

        User currentUser = new User();
        currentUser.setUsername("testuser");

        List<MessageDto> expectedMessages = new ArrayList<>();
        expectedMessages.add(new MessageDto("Receiver 1", "Message 1"));
        expectedMessages.add(new MessageDto("Receiver 2", "Message 2"));

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(messageService.getAllMessagesForUser(currentUser)).thenReturn(expectedMessages);


        List<MessageDto> actualMessages = messageController.retrieveAllMessages();


        assertEquals(expectedMessages, actualMessages);
    }

    @Test
    void sendMessage_withValidMessageDto_shouldReturnOkResponse() {

        User currentUser = new User();
        currentUser.setUsername("testuser");

        User receiver = new User();
        receiver.setUsername("receiver");

        MessageSendRequest messageDto = new MessageSendRequest();
        messageDto.setReceiver("receiver");
        messageDto.setText("Test message");

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(userService.loadUserByUsername(messageDto.getReceiver())).thenReturn(receiver);
        doNothing().when(messageService).sendMessage(currentUser, receiver, messageDto.getText());


        ResponseEntity<String> response = messageController.sendMessage(messageDto);


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Your message was delivered", response.getBody());


        verify(userService).getCurrentUser();
        verify(userService).loadUserByUsername(messageDto.getReceiver());
        verify(messageService).sendMessage(currentUser, receiver, messageDto.getText());
    }
}