package com.baron.SocialMediaAPI.util;

import com.baron.SocialMediaAPI.dto.MessageDto;
import com.baron.SocialMediaAPI.model.Message;

public class MessageUtil {

    public static MessageDto mapMessageToMessageDto(Message message){

        return MessageDto.builder()
                .sender(message.getSenderUsername())
                .text(message.getText()).id(message.getId())
                        .receiver(message.getReceiverUsername()).
                build();
    }
}
