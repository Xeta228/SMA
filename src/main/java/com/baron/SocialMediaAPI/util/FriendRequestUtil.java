package com.baron.SocialMediaAPI.util;

import com.baron.SocialMediaAPI.dto.FriendRequestDto;
import com.baron.SocialMediaAPI.dto.UserDto;
import com.baron.SocialMediaAPI.model.FriendRequest;
import com.baron.SocialMediaAPI.model.User;

public class FriendRequestUtil {



        public static FriendRequestDto mapFriendRequestToFriendRequestDto(FriendRequest friendRequest){
            return FriendRequestDto.builder()
                    .senderId(friendRequest.getSender().getId())
                    .senderName(friendRequest.getSender().getUsername())
                    .receiverName(friendRequest.getReceiver().getUsername())
                    .build();
        }


}
