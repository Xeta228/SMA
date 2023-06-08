package com.baron.SocialMediaAPI.service;


import com.baron.SocialMediaAPI.dto.MessageDto;
import com.baron.SocialMediaAPI.model.Message;
import com.baron.SocialMediaAPI.model.User;
import com.baron.SocialMediaAPI.repository.MessageRepository;
import com.baron.SocialMediaAPI.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserService userService;

    public List<MessageDto> getAllInboxMessagesForUser(String username) {
        List<Message> inboxMessages = messageRepository.findAllByReceiverUsername(username);
        return mapMessagesToMessageDtos(inboxMessages);
    }

    public void save(Message message) {
        messageRepository.save(message);
    }

    public List<MessageDto> getAllMessagesForUser(User curr) {
        List<Message> messages = messageRepository.findBySenderUsernameOrReceiverUsername(curr.getUsername(),
                curr.getUsername());
        return mapMessagesToMessageDtos(messages);
    }

    public List<MessageDto> getChatHistory(User curr, String chatPartnerUsername) {
        List<Message> messages = messageRepository.findBySenderUsernameAndReceiverUsername(curr.getUsername(),
                chatPartnerUsername);
        return mapMessagesToMessageDtos(messages);
    }

    public void sendMessage(User sender, User receiver, String text) {
        if (receiver == null) {
            throw new UsernameNotFoundException("Specified user does not exist!");
        }

        Message message = Message.builder()
                .senderUsername(sender.getUsername())
                .receiverUsername(receiver.getUsername())
                .text(text)
                .build();
        save(message);
    }

    private List<MessageDto> mapMessagesToMessageDtos(List<Message> messages) {
        return messages.stream()
                .map(MessageUtil::mapMessageToMessageDto)
                .toList();
    }
}
