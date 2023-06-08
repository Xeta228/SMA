package com.baron.SocialMediaAPI.controller;


import com.baron.SocialMediaAPI.dto.MessageDto;
import com.baron.SocialMediaAPI.dto.MessageSendRequest;
import com.baron.SocialMediaAPI.model.User;
import com.baron.SocialMediaAPI.service.MessageService;
import com.baron.SocialMediaAPI.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final UserService userService;

    @GetMapping("/inbox")
    @Operation(summary = "Get all inbox messages for currently logged-in user")
    public List<MessageDto> retrieveAllInboxMessages() {
        User curr = userService.getCurrentUser();

        return messageService.getAllInboxMessagesForUser(curr.getUsername());
    }

    @GetMapping
    @Operation(summary = "Get all messages user has ever sent or received")
    public List<MessageDto> retrieveAllMessages() {
        User curr = userService.getCurrentUser();

        return messageService.getAllMessagesForUser(curr);
    }

    @GetMapping("/{friendId}")
    @Operation(summary = "Get chat history with specified user")
    public List<MessageDto> retrieveAllChatHistory(@PathVariable String friendId) {
        User curr = userService.getCurrentUser();

        return messageService.getChatHistory(curr,friendId);
    }

    @PostMapping
    @Operation(summary = "Send new message")
    public ResponseEntity<String> sendMessage(@RequestBody @Valid MessageSendRequest messageSendRequest) {
        User curr = userService.getCurrentUser();
        User receiver = (User)userService.loadUserByUsername(messageSendRequest.getReceiver());

        messageService.sendMessage(curr, receiver, messageSendRequest.getText());
        return ResponseEntity.ok("Your message was delivered");
    }

}
