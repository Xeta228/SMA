package com.baron.SocialMediaAPI.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendRequestDto{

    private Long senderId;
    private String senderName;
    private String receiverName;


}
